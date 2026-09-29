package com.disi.backend.service;

import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserProfileRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private FeedService feedService;

    private User currentUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(1L);
        currentUser.setEmail("user@test.com");
        currentUser.setUsername("currentuser");

        friendUser = new User();
        friendUser.setUserId(2L);
        friendUser.setEmail("friend@test.com");
        friendUser.setUsername("frienduser");
    }

    @Test
    void getFeed_WithFriendPosts_ReturnsCombinedFeed() {
        Friendship friendship = new Friendship();
        friendship.setRequester(currentUser);
        friendship.setAddressee(friendUser);
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        Post friendPost = new Post();
        friendPost.setId(10L);
        friendPost.setUser(friendUser);
        friendPost.setContent("Friend's post");
        friendPost.setCreatedAt(LocalDateTime.now());
        friendPost.setUpdatedAt(LocalDateTime.now());

        Post generalPost = new Post();
        generalPost.setId(11L);
        User stranger = new User();
        stranger.setUserId(3L);
        stranger.setUsername("stranger");
        generalPost.setUser(stranger);
        generalPost.setContent("Stranger's post");
        generalPost.setCreatedAt(LocalDateTime.now().minusHours(1));
        generalPost.setUpdatedAt(LocalDateTime.now().minusHours(1));

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED))
                .thenReturn(List.of(friendship));
        when(postRepository.findPostsByFriends(any(), any()))
                .thenReturn(new PageImpl<>(List.of(friendPost)));
        when(postRepository.findPostsExcludingUsers(any(), any()))
                .thenReturn(new PageImpl<>(List.of(generalPost)));
        when(userProfileRepository.findByUserId(any())).thenReturn(Optional.empty());

        List<PostResponse> feed = feedService.getFeed("user@test.com", 0, 10);

        assertNotNull(feed);
        assertFalse(feed.isEmpty());
        assertEquals(friendPost.getId(), feed.get(0).getId());
    }

    @Test
    void getFeed_NoFriends_ReturnsOnlyGeneralPosts() {
        Post generalPost = new Post();
        generalPost.setId(11L);
        User stranger = new User();
        stranger.setUserId(3L);
        stranger.setUsername("stranger");
        generalPost.setUser(stranger);
        generalPost.setContent("General post");
        generalPost.setCreatedAt(LocalDateTime.now());
        generalPost.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED))
                .thenReturn(Collections.emptyList());
        when(postRepository.findPostsExcludingUsers(any(), any()))
                .thenReturn(new PageImpl<>(List.of(generalPost)));
        when(userProfileRepository.findByUserId(any())).thenReturn(Optional.empty());

        List<PostResponse> feed = feedService.getFeed("user@test.com", 0, 10);

        assertEquals(1, feed.size());
        assertEquals("General post", feed.get(0).getContent());
    }

    @Test
    void getFeed_UserNotFound_Throws404() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> feedService.getFeed("unknown@test.com", 0, 10));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getFeed_PendingFriendships_ExcludedFromFriendPosts() {
        Friendship pendingFriendship = new Friendship();
        pendingFriendship.setRequester(currentUser);
        pendingFriendship.setAddressee(friendUser);
        pendingFriendship.setStatus(FriendshipStatus.PENDING);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED))
                .thenReturn(List.of(pendingFriendship));
        when(postRepository.findPostsExcludingUsers(any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<PostResponse> feed = feedService.getFeed("user@test.com", 0, 10);

        verify(postRepository, never()).findPostsByFriends(any(), any());
    }

    @Test
    void getFeed_EmptyFeed_ReturnsEmptyList() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED))
                .thenReturn(Collections.emptyList());
        when(postRepository.findPostsExcludingUsers(any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<PostResponse> feed = feedService.getFeed("user@test.com", 0, 10);

        assertTrue(feed.isEmpty());
    }
}
