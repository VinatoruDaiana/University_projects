package com.disi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageQueueEvent {
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
}