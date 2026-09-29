package com.disi.backend.controller;

import com.disi.backend.requests.*;
import com.disi.backend.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<MessageResponse> sendMessage(Authentication auth, @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(auth.getName(), request));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<List<MessageResponse>> getConversation(Authentication auth, @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getConversation(auth.getName(), userId));
    }

    @GetMapping
    @PreAuthorize("@authorizationService.isConnected(authentication.name)")
    public ResponseEntity<List<ConversationResponse>> getConversationsList(Authentication auth) {
        return ResponseEntity.ok(messageService.getConversationsList(auth.getName()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleExceptions(ResponseStatusException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }
}