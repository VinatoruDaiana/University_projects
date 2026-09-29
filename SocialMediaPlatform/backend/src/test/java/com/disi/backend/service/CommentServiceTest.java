package com.disi.backend.service;

import com.disi.backend.entity.*;
import com.disi.backend.repository.CommentLikeRepository;
import com.disi.backend.repository.CommentRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.CommentRequest;
import com.disi.backend.requests.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("user@test.com");
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(10L);
        testPost.setUser(testUser);

        testComment = new Comment();
        testComment.setCommentId(100L);
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setContent("Nice post!");
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
    }


    @Test
    void createComment_PostNotFound_Throws404() {
        CommentRequest request = new CommentRequest();
        request.setContent("comment");

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.createComment(999L, request, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getCommentsForPost_ReturnsSortedByLikes() {
        Comment comment2 = new Comment();
        comment2.setCommentId(101L);
        comment2.setPost(testPost);
        comment2.setUser(testUser);
        comment2.setContent("Another comment");
        comment2.setCreatedAt(LocalDateTime.now());
        comment2.setUpdatedAt(LocalDateTime.now());

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentRepository.findByPostOrderByCreatedAtAsc(testPost)).thenReturn(List.of(testComment, comment2));
        when(commentLikeRepository.countByComment(testComment)).thenReturn(5);
        when(commentLikeRepository.countByComment(comment2)).thenReturn(10);
        when(commentLikeRepository.existsByCommentAndUser(any(), any())).thenReturn(false);

        List<CommentResponse> comments = commentService.getCommentsForPost(10L, "user@test.com");

        assertEquals(2, comments.size());
        assertEquals(10, comments.get(0).getLikeCount());
        assertEquals(5, comments.get(1).getLikeCount());
    }

    @Test
    void getCommentsForPost_PostNotFound_Throws404() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.getCommentsForPost(999L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateComment_Authorized_Updates() {
        CommentRequest request = new CommentRequest();
        request.setContent("Updated comment");

        when(authorizationService.canEditComment("user@test.com", 100L)).thenReturn(true);
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentLikeRepository.countByComment(testComment)).thenReturn(0);
        when(commentLikeRepository.existsByCommentAndUser(testComment, testUser)).thenReturn(false);

        CommentResponse response = commentService.updateComment(100L, request, "user@test.com");

        assertNotNull(response);
        assertEquals("Updated comment", response.getContent());
    }

    @Test
    void updateComment_Unauthorized_Throws403() {
        CommentRequest request = new CommentRequest();
        request.setContent("hack");

        when(authorizationService.canEditComment("other@test.com", 100L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.updateComment(100L, request, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deleteComment_Authorized_Deletes() {
        when(authorizationService.canDeleteComment("user@test.com", 100L)).thenReturn(true);
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(100L, "user@test.com");

        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteComment_Unauthorized_Throws403() {
        when(authorizationService.canDeleteComment("other@test.com", 100L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.deleteComment(100L, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void likeComment_Success() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentLikeRepository.existsByCommentAndUser(testComment, testUser)).thenReturn(false);

        commentService.likeComment(100L, "user@test.com");

        verify(commentLikeRepository).save(any(CommentLike.class));
    }

    @Test
    void likeComment_AlreadyLiked_ThrowsConflict() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentLikeRepository.existsByCommentAndUser(testComment, testUser)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.likeComment(100L, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void likeComment_CommentNotFound_Throws404() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.likeComment(999L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void unlikeComment_Success() {
        CommentLike like = new CommentLike();
        like.setComment(testComment);
        like.setUser(testUser);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentLikeRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(like));

        commentService.unlikeComment(100L, "user@test.com");

        verify(commentLikeRepository).delete(like);
    }

    @Test
    void unlikeComment_NotLiked_Throws404() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(commentLikeRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.unlikeComment(100L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
