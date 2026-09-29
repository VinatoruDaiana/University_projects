package com.example.websocket.controllers;

import com.example.websocket.dtos.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class ChatWsController {

    private final RabbitTemplate rabbitTemplate;

    @Value("${websocket.queues.chatUserIn}")
    private String chatUserIn;

    public ChatWsController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Frontend -> /app/chat/send
    @MessageMapping("/chat/send")
    public void send(ChatMessage msg) {
        if (msg == null) return;

        if (msg.getChatId() == null || msg.getChatId().isBlank()) {
            msg.setChatId(UUID.randomUUID().toString());
        }
        if (msg.getFrom() == null || msg.getFrom().isBlank()) {
            msg.setFrom("USER");
        }
        if (msg.getTimestamp() == null) {
            msg.setTimestamp(System.currentTimeMillis());
        }

        // IMPORTANT: userIdentifier e obligatoriu (altfel nu poți ruta)
        if (msg.getUserIdentifier() == null || msg.getUserIdentifier().isBlank()) {
            return;
        }

        rabbitTemplate.convertAndSend(chatUserIn, msg);
    }
}
