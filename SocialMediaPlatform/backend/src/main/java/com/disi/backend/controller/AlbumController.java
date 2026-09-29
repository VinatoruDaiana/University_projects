package com.disi.backend.controller;

import com.disi.backend.dto.AlbumRequest;
import com.disi.backend.dto.AlbumResponse;
import com.disi.backend.requests.PostResponse;
import com.disi.backend.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping("/users/{userId}/albums")
    public ResponseEntity<List<AlbumResponse>> getUserAlbums(@PathVariable Long userId) {
        return ResponseEntity.ok(albumService.getUserAlbums(userId));
    }

    @PostMapping("/albums")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<AlbumResponse> createAlbum(
            Authentication authentication,
            @Valid @RequestBody AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumService.createAlbum(authentication.getName(), request));
    }

    @PatchMapping("/albums/{albumId}")
    @PreAuthorize("@authorizationService.canOperateAlbum(authentication.name, #albumId)")
    public ResponseEntity<AlbumResponse> updateAlbum(
            @PathVariable Long albumId,
            Authentication authentication,
            @Valid @RequestBody AlbumRequest request) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, authentication.getName(), request));
    }

    @DeleteMapping("/albums/{albumId}")
    @PreAuthorize("@authorizationService.canOperateAlbum(authentication.name, #albumId)")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable Long albumId,
            Authentication authentication) {
        albumService.deleteAlbum(albumId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/albums/{albumId}/posts")
    @PreAuthorize("@authorizationService.canOperateAlbum(authentication.name, #albumId)")
    public ResponseEntity<List<PostResponse>> getAlbumPosts(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumPosts(albumId));
    }

    @PostMapping("/albums/{albumId}/posts/{postId}")
    @PreAuthorize("@authorizationService.canOperateAlbum(authentication.name, #albumId)")
    public ResponseEntity<PostResponse> addPostToAlbum(
            @PathVariable Long albumId,
            @PathVariable Long postId,
            Authentication authentication) {
        return ResponseEntity.ok(albumService.addPostToAlbum(albumId, postId, authentication.getName()));
    }
}