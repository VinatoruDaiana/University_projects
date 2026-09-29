package com.disi.backend.requests;

import com.disi.backend.entity.Message;
import lombok.Getter;

@Getter
public class MessageResponse {
    private final Long messageId;
    private final Long senderId;
    private final Long receiverId;
    private final String content;
    private final String createdAt;
    private final Boolean read;

    public MessageResponse(Message message) {
        this.messageId = message.getMessageId();
        this.senderId = message.getSender().getUserId();
        this.receiverId = message.getReceiver().getUserId();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt() != null ? message.getCreatedAt().toString() : null;
        this.read = message.getIsRead();
    }
}