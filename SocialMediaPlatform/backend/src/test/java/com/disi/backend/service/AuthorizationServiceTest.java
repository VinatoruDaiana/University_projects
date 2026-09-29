package com.disi.backend.service;

import com.disi.backend.entity.*;
import com.disi.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        normalUser = new User();
        normalUser.setUserId(1L);
        normalUser.setEmail("user@test.com");
        normalUser.setRole("USER");
        normalUser.setIsBanned(false);

        adminUser = new User();
        adminUser.setUserId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole("ADMIN");
        adminUser.setIsBanned(false);
    }

    @Test
    void isAdmin_AdminUser_ReturnsTrue() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        assertTrue(authorizationService.isAdmin("admin@test.com"));
    }

    @Test
    void isAdmin_NormalUser_ReturnsFalse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertFalse(authorizationService.isAdmin("user@test.com"));
    }

    @Test
    void isAdmin_NullEmail_ReturnsFalse() {
        assertFalse(authorizationService.isAdmin(null));
    }

    @Test
    void isConnected_ActiveUser_ReturnsTrue() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertTrue(authorizationService.isConnected("user@test.com"));
    }

    @Test
    void isConnected_BannedUser_ReturnsFalse() {
        normalUser.setIsBanned(true);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertFalse(authorizationService.isConnected("user@test.com"));
    }

    @Test
    void canOperateAccount_OwnAccount_ReturnsTrue() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertTrue(authorizationService.canOperateAccount("user@test.com", 1L));
    }

    @Test
    void canOperateAccount_OtherAccount_ReturnsFalse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertFalse(authorizationService.canOperateAccount("user@test.com", 99L));
    }

    @Test
    void canOperateAccount_Admin_CanOperateAny() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        assertTrue(authorizationService.canOperateAccount("admin@test.com", 99L));
    }

    @Test
    void canOperatePost_Owner_ReturnsTrue() {
        Post post = new Post();
        post.setId(10L);
        post.setUser(normalUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertTrue(authorizationService.canOperatePost("user@test.com", 10L));
    }

    @Test
    void canOperatePost_NonOwner_ReturnsFalse() {
        User otherUser = new User();
        otherUser.setUserId(99L);

        Post post = new Post();
        post.setId(10L);
        post.setUser(otherUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertFalse(authorizationService.canOperatePost("user@test.com", 10L));
    }

    @Test
    void canOperatePost_Admin_ReturnsTrue() {
        User otherUser = new User();
        otherUser.setUserId(99L);

        Post post = new Post();
        post.setId(10L);
        post.setUser(otherUser);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertTrue(authorizationService.canOperatePost("admin@test.com", 10L));
    }

    @Test
    void canOperatePost_PostNotFound_ReturnsFalse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertFalse(authorizationService.canOperatePost("user@test.com", 999L));
    }

    @Test
    void canEditComment_Owner_ReturnsTrue() {
        Comment comment = new Comment();
        comment.setCommentId(100L);
        comment.setUser(normalUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertTrue(authorizationService.canEditComment("user@test.com", 100L));
    }

    @Test
    void canEditComment_NonOwner_ReturnsFalse() {
        User otherUser = new User();
        otherUser.setUserId(99L);

        Comment comment = new Comment();
        comment.setCommentId(100L);
        comment.setUser(otherUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertFalse(authorizationService.canEditComment("user@test.com", 100L));
    }

    @Test
    void canDeleteComment_CommentOwner_ReturnsTrue() {
        Post post = new Post();
        post.setUser(adminUser);

        Comment comment = new Comment();
        comment.setCommentId(100L);
        comment.setUser(normalUser);
        comment.setPost(post);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertTrue(authorizationService.canDeleteComment("user@test.com", 100L));
    }

    @Test
    void canDeleteComment_PostOwner_ReturnsTrue() {
        Post post = new Post();
        post.setUser(normalUser);

        User commentAuthor = new User();
        commentAuthor.setUserId(99L);

        Comment comment = new Comment();
        comment.setCommentId(100L);
        comment.setUser(commentAuthor);
        comment.setPost(post);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertTrue(authorizationService.canDeleteComment("user@test.com", 100L));
    }

    @Test
    void canDeleteComment_UnrelatedUser_ReturnsFalse() {
        User postOwner = new User();
        postOwner.setUserId(50L);
        Post post = new Post();
        post.setUser(postOwner);

        User commentAuthor = new User();
        commentAuthor.setUserId(99L);

        Comment comment = new Comment();
        comment.setCommentId(100L);
        comment.setUser(commentAuthor);
        comment.setPost(post);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertFalse(authorizationService.canDeleteComment("user@test.com", 100L));
    }

    @Test
    void canLikePost_OtherUsersPost_ReturnsTrue() {
        User postOwner = new User();
        postOwner.setUserId(99L);
        Post post = new Post();
        post.setId(10L);
        post.setUser(postOwner);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertTrue(authorizationService.canLikePost("user@test.com", 10L));
    }

    @Test
    void canLikePost_OwnPost_ReturnsFalse() {
        Post post = new Post();
        post.setId(10L);
        post.setUser(normalUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertFalse(authorizationService.canLikePost("user@test.com", 10L));
    }

    @Test
    void canLikePost_BannedUser_ReturnsFalse() {
        normalUser.setIsBanned(true);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));

        assertFalse(authorizationService.canLikePost("user@test.com", 10L));
    }

    @Test
    void canOperateAlbum_Owner_ReturnsTrue() {
        Album album = new Album();
        album.setUserId(1L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));

        assertTrue(authorizationService.canOperateAlbum("user@test.com", 5L));
    }

    @Test
    void canOperatePhoto_Owner_ReturnsTrue() {
        Photo photo = new Photo();
        photo.setId(5L);
        photo.setUploadedBy(normalUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(photoRepository.findById(5L)).thenReturn(Optional.of(photo));

        assertTrue(authorizationService.canOperatePhoto("user@test.com", 5L));
    }

    @Test
    void canOperateFriendship_Participant_ReturnsTrue() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(normalUser);
        friendship.setAddressee(adminUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));

        assertTrue(authorizationService.canOperateFriendship("user@test.com", 50L));
    }

    @Test
    void canOperateFriendship_NonParticipant_ReturnsFalse() {
        User other1 = new User();
        other1.setUserId(50L);
        User other2 = new User();
        other2.setUserId(60L);

        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(other1);
        friendship.setAddressee(other2);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));

        assertFalse(authorizationService.canOperateFriendship("user@test.com", 50L));
    }

    @Test
    void canOperateAccount_NullParams_ReturnsFalse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertFalse(authorizationService.canOperateAccount("user@test.com", null));
    }

    @Test
    void canOperatePost_NullParams_ReturnsFalse() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(normalUser));
        assertFalse(authorizationService.canOperatePost("user@test.com", null));
    }
}
