package com.disi.backend.controller;

import com.disi.backend.requests.CommentRequest;
import com.disi.backend.requests.CommentResponse;
import com.disi.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, request, principal.getName()));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId, Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(commentService.getCommentsForPost(postId, email));
    }

    @PatchMapping("/comments/{commentId}")
    @PreAuthorize("@authorizationService.canEditComment(authentication.name, #commentId)")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentRequest request, Principal principal) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, principal.getName()));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("@authorizationService.canDeleteComment(authentication.name, #commentId)")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Comment deleted"));
    }

    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<Map<String, String>> likeComment(@PathVariable Long commentId, Principal principal) {
        commentService.likeComment(commentId, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Comment liked"));
    }

    @DeleteMapping("/comments/{commentId}/like")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<Map<String, String>> unlikeComment(@PathVariable Long commentId, Principal principal) {
        commentService.unlikeComment(commentId, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Comment unliked"));
    }
}