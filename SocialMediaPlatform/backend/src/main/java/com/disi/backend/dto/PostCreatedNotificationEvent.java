package com.disi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedNotificationEvent {
    private Long senderId;
    private Long postId;
    private String senderUsername;
    private LocalDateTime timestamp;
}
