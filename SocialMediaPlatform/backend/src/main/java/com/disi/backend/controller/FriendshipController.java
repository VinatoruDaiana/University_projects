package com.disi.backend.controller;

import com.disi.backend.dto.FriendshipDTO;
import com.disi.backend.entity.User;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<?> getFriendsList(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentUserEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            List<FriendshipDTO> friendsList = friendshipService.getFriendsList(userOptional.get());
            return ResponseEntity.ok(friendsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }
    }

    @PostMapping("/request/{addresseeId}")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String addresseeId, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(currentUserEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            friendshipService.sendFriendRequest(userOptional.get(), addresseeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Friend request sent"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Server Error"));
        }
    }

    @PatchMapping("/{requestId}/accept")
    @PreAuthorize("@authorizationService.canOperateFriendship(authentication.name, #requestId)")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            friendshipService.acceptFriendRequest(requestId, authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Friend request accepted"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Server Error"));
        }
    }

    @PatchMapping("/{requestId}/decline")
    @PreAuthorize("@authorizationService.canOperateFriendship(authentication.name, #requestId)")
    public ResponseEntity<?> declineFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            friendshipService.declineFriendRequest(requestId, authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Friend request declined"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Server Error"));
        }
    }
}