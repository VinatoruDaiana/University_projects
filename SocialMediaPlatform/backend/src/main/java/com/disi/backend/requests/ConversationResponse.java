package com.disi.backend.requests;

import com.disi.backend.entity.User;
import lombok.Getter;

@Getter
public class ConversationResponse {
    private final Long userId;
    private final String username;
    private final String email;
    private final String role;

    public ConversationResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}