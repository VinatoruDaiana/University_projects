package com.disi.backend.controller;

import com.disi.backend.dto.UserProfileResponse;
import com.disi.backend.requests.PhotoResponse;
import com.disi.backend.requests.PostResponse;
import com.disi.backend.requests.BanRequest;
import com.disi.backend.requests.UpdateProfileRequest;
import com.disi.backend.service.PhotoService;
import com.disi.backend.service.PostService;
import com.disi.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PhotoService photoService;

    @GetMapping("/")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithProfiles());
    }

    @PatchMapping("/{userId}/ban")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Void> banUser(@PathVariable Long userId, @RequestBody BanRequest request) {
        userService.banUser(userId, request.getReason());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/unban")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        userService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("@authorizationService.canOperateAccount(authentication.name, #userId)")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request,
            Principal principal) {
        UserProfileResponse response = userService.updateProfile(userId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(postService.getUserPosts(userId, email));
    }

    @GetMapping("/{userId}/photos")
    public ResponseEntity<List<PhotoResponse>> getUserPhotos(@PathVariable Long userId) {
        return ResponseEntity.ok(photoService.getUserPhotos(userId));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfileByUsername(username));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfileById(userId));
    }
    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam("q") String query, Principal principal) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        String currentUserEmail = principal.getName();
        return ResponseEntity.ok(userService.searchUsers(query, currentUserEmail));
    }

    @PostMapping("/{userId}/profile/photo")
    @PreAuthorize("@authorizationService.canOperateAccount(authentication.name, #userId)")
    public ResponseEntity<UserProfileResponse> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePhoto(userId, file));
    }
}