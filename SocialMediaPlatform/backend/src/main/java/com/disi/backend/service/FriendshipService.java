package com.disi.backend.service;

import com.disi.backend.dto.FriendshipDTO;
import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FriendshipDTO> getFriendsList(User currentUser) {
        List<Friendship> friendships = friendshipRepository.findActiveOrPendingFriendships(currentUser, FriendshipStatus.DECLINED);

        return friendships.stream().map(friendship -> {
            boolean isCurrentUserRequester = friendship.getRequester().getUserId().equals(currentUser.getUserId());
            User otherUser = isCurrentUserRequester ? friendship.getAddressee() : friendship.getRequester();

            return new FriendshipDTO(
                    friendship.getId().toString(),
                    friendship.getRequester().getUserId().toString(),
                    friendship.getAddressee().getUserId().toString(),
                    friendship.getStatus().name(),
                    otherUser.getUserId().toString(),
                    otherUser.getUsername()
            );
        }).collect(Collectors.toList());
    }

    public void sendFriendRequest(User requester, String addresseeIdString) {
        Long addresseeId;
        try {
            addresseeId = Long.parseLong(addresseeIdString);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid User ID format");
        }

        if (requester.getUserId().equals(addresseeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot send a friend request to yourself");
        }

        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user does not exist"));

        Optional<Friendship> existingFriendship = friendshipRepository.findFriendshipBetweenUsers(requester, addressee);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();

            if (friendship.getStatus() == FriendshipStatus.DECLINED) {
                friendship.setStatus(FriendshipStatus.PENDING);
                friendship.setRequester(requester);
                friendship.setAddressee(addressee);
                friendshipRepository.save(friendship);
                return;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Friend request already exists");
            }
        }

        Friendship newFriendship = new Friendship();
        newFriendship.setRequester(requester);
        newFriendship.setAddressee(addressee);
        newFriendship.setStatus(FriendshipStatus.PENDING);

        friendshipRepository.save(newFriendship);
    }

    public void acceptFriendRequest(Long requestId, String userEmail) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!friendship.getAddressee().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the addressee can accept this request");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    public void declineFriendRequest(Long requestId, String userEmail) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!friendship.getAddressee().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the addressee can decline this request");
        }

        friendshipRepository.delete(friendship);
    }
}