package com.disi.backend.controller;

import com.disi.backend.requests.PostResponse;
import com.disi.backend.requests.PostRequest;
import com.disi.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.disi.backend.requests.LikeResponse;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request, Principal principal) {
        PostResponse response = postService.createPost(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, Principal principal) {

        PostResponse response = postService.getPost(id, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@authorizationService.canOperatePost(authentication.name, #id)")
    public ResponseEntity<Map<String, String>> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request, Principal principal) {
        postService.updatePost(id, request, principal.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.canOperatePost(authentication.name, #id)")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleExceptions(ResponseStatusException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<LikeResponse> likePost(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(postService.likePost(id, principal.getName()));
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<Void> unlikePost(@PathVariable Long id, Principal principal) {
        postService.unlikePost(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}