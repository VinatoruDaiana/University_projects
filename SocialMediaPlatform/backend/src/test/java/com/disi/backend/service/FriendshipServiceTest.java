package com.disi.backend.service;

import com.disi.backend.dto.FriendshipDTO;
import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private User requester;
    private User addressee;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setUserId(1L);
        requester.setEmail("requester@test.com");
        requester.setUsername("requester");

        addressee = new User();
        addressee.setUserId(2L);
        addressee.setEmail("addressee@test.com");
        addressee.setUsername("addressee");
    }

    @Test
    void sendFriendRequest_Success_CreatesPending() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
        when(friendshipRepository.findFriendshipBetweenUsers(requester, addressee)).thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendship.class))).thenAnswer(inv -> inv.getArgument(0));

        friendshipService.sendFriendRequest(requester, "2");

        verify(friendshipRepository).save(argThat(f ->
                f.getRequester().equals(requester) &&
                f.getAddressee().equals(addressee) &&
                f.getStatus() == FriendshipStatus.PENDING
        ));
    }

    @Test
    void sendFriendRequest_ToSelf_Throws403() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.sendFriendRequest(requester, "1"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void sendFriendRequest_InvalidIdFormat_Throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.sendFriendRequest(requester, "not-a-number"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void sendFriendRequest_UserNotFound_Throws404() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.sendFriendRequest(requester, "999"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void sendFriendRequest_AlreadyExists_ThrowsConflict() {
        Friendship existing = new Friendship();
        existing.setStatus(FriendshipStatus.PENDING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
        when(friendshipRepository.findFriendshipBetweenUsers(requester, addressee)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.sendFriendRequest(requester, "2"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void sendFriendRequest_PreviouslyDeclined_ReactivatesPending() {
        Friendship declined = new Friendship();
        declined.setStatus(FriendshipStatus.DECLINED);
        declined.setRequester(addressee);
        declined.setAddressee(requester);

        when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
        when(friendshipRepository.findFriendshipBetweenUsers(requester, addressee)).thenReturn(Optional.of(declined));

        friendshipService.sendFriendRequest(requester, "2");

        assertEquals(FriendshipStatus.PENDING, declined.getStatus());
        assertEquals(requester, declined.getRequester());
        assertEquals(addressee, declined.getAddressee());
        verify(friendshipRepository).save(declined);
    }

    @Test
    void acceptFriendRequest_AsAddressee_Accepts() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));
        when(userRepository.findByEmail("addressee@test.com")).thenReturn(Optional.of(addressee));

        friendshipService.acceptFriendRequest(50L, "addressee@test.com");

        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void acceptFriendRequest_AsRequester_Throws403() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));
        when(userRepository.findByEmail("requester@test.com")).thenReturn(Optional.of(requester));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.acceptFriendRequest(50L, "requester@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void acceptFriendRequest_NotFound_Throws404() {
        when(friendshipRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.acceptFriendRequest(999L, "addressee@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void declineFriendRequest_AsAddressee_Deletes() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));
        when(userRepository.findByEmail("addressee@test.com")).thenReturn(Optional.of(addressee));

        friendshipService.declineFriendRequest(50L, "addressee@test.com");

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void declineFriendRequest_AsRequester_Throws403() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);

        when(friendshipRepository.findById(50L)).thenReturn(Optional.of(friendship));
        when(userRepository.findByEmail("requester@test.com")).thenReturn(Optional.of(requester));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> friendshipService.declineFriendRequest(50L, "requester@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void getFriendsList_ReturnsCorrectDTOs() {
        Friendship friendship = new Friendship();
        friendship.setId(50L);
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        when(friendshipRepository.findActiveOrPendingFriendships(requester, FriendshipStatus.DECLINED))
                .thenReturn(List.of(friendship));

        List<FriendshipDTO> result = friendshipService.getFriendsList(requester);

        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getOther_user_id());
        assertEquals("addressee", result.get(0).getOther_user_username());
        assertEquals("ACCEPTED", result.get(0).getStatus());
    }
}
