package com.disi.backend.controller;

import com.disi.backend.requests.PasswordResetConfirm;
import com.disi.backend.requests.PasswordResetRequest;
import com.disi.backend.requests.RegisterRequest;
import com.disi.backend.entity.User;
import com.disi.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.disi.backend.requests.LoginRequest;
import com.disi.backend.requests.LoginResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request);
            Map<String, String> response = new HashMap<>();
            response.put("id", user.getUserId().toString());
            response.put("email", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "If that email is registered, you will receive a reset link shortly.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<?> confirmPasswordReset(
            @RequestParam String token,
            @Valid @RequestBody PasswordResetConfirm request) {
        try {
            authService.confirmPasswordReset(token, request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful.");
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(error);
        }
    }

}