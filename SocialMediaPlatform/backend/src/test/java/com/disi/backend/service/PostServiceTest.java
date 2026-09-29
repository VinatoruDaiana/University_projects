package com.disi.backend.service;

import com.disi.backend.entity.Photo;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.PostLike;
import com.disi.backend.entity.User;
import com.disi.backend.repository.PhotoRepository;
import com.disi.backend.repository.PostLikeRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.LikeResponse;
import com.disi.backend.requests.PostRequest;
import com.disi.backend.requests.PostResponse;
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
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("user@test.com");
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(10L);
        testPost.setUser(testUser);
        testPost.setContent("Hello world");
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());
    }





    @Test
    void createPost_PhotoNotFound_Throws404() {
        PostRequest request = new PostRequest();
        request.setContent("Photo post");
        request.setPhotoId(999L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(photoRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createPost_UserNotFound_Throws401() {
        PostRequest request = new PostRequest();
        request.setContent("test");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "unknown@test.com"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getPost_Exists_ReturnsResponse() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.countByPost(testPost)).thenReturn(3L);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(postLikeRepository.existsByPostAndUser(testPost, testUser)).thenReturn(true);

        PostResponse response = postService.getPost(10L, "user@test.com");

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(3, response.getLikeCount());
        assertTrue(response.isLikedByCurrentUser());
    }

    @Test
    void getPost_NotFound_Throws404() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.getPost(999L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deletePost_Authorized_Deletes() {
        when(authorizationService.canOperatePost("user@test.com", 10L)).thenReturn(true);
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        postService.deletePost(10L, "user@test.com");

        verify(postRepository).delete(testPost);
    }

    @Test
    void deletePost_Unauthorized_Throws403() {
        when(authorizationService.canOperatePost("other@test.com", 10L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.deletePost(10L, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deletePost_WithPhoto_DeletesBoth() {
        Photo photo = new Photo();
        photo.setId(5L);
        testPost.setPhoto(photo);

        when(authorizationService.canOperatePost("user@test.com", 10L)).thenReturn(true);
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        postService.deletePost(10L, "user@test.com");

        verify(postRepository).delete(testPost);
        verify(photoRepository).delete(photo);
    }

    @Test
    void updatePost_Authorized_Updates() {
        PostRequest request = new PostRequest();
        request.setContent("Updated content");

        when(authorizationService.canOperatePost("user@test.com", 10L)).thenReturn(true);
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        postService.updatePost(10L, request, "user@test.com");

        assertEquals("Updated content", testPost.getContent());
        verify(postRepository).save(testPost);
    }

    @Test
    void updatePost_Unauthorized_Throws403() {
        PostRequest request = new PostRequest();
        request.setContent("hack");

        when(authorizationService.canOperatePost("other@test.com", 10L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.updatePost(10L, request, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void likePost_Success_ReturnsLikeResponse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByPostAndUser(testPost, testUser)).thenReturn(false);
        when(postLikeRepository.save(any(PostLike.class))).thenReturn(new PostLike());

        LikeResponse response = postService.likePost(10L, "user@test.com");

        assertNotNull(response);
        assertEquals(10L, response.getPostId());
        assertTrue(response.isLiked());
    }

    @Test
    void likePost_AlreadyLiked_ThrowsConflict() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.existsByPostAndUser(testPost, testUser)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.likePost(10L, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void unlikePost_Success_Deletes() {
        PostLike like = new PostLike();
        like.setPost(testPost);
        like.setUser(testUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(like));

        postService.unlikePost(10L, "user@test.com");

        verify(postLikeRepository).delete(like);
    }

    @Test
    void unlikePost_NotLiked_Throws404() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postLikeRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.unlikePost(10L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getUserPosts_ReturnsPostsList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(List.of(testPost));
        when(postLikeRepository.countByPost(testPost)).thenReturn(0L);

        List<PostResponse> posts = postService.getUserPosts(1L, "user@test.com");

        assertEquals(1, posts.size());
        assertEquals(10L, posts.get(0).getId());
    }

    @Test
    void getUserPosts_UserNotFound_Throws404() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.getUserPosts(999L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getAllPosts_ReturnsSortedByDate() {
        Post olderPost = new Post();
        olderPost.setId(9L);
        olderPost.setUser(testUser);
        olderPost.setContent("older");
        olderPost.setCreatedAt(LocalDateTime.now().minusDays(1));
        olderPost.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(postRepository.findAll()).thenReturn(List.of(olderPost, testPost));

        List<PostResponse> posts = postService.getAllPosts();

        assertEquals(2, posts.size());
        assertEquals(testPost.getId(), posts.get(0).getId());
    }
}
