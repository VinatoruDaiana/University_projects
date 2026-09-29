package com.example.websocket.messaging;

import com.example.websocket.dtos.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Consumes chat events produced by customer-support-service and forwards them to WebSocket topics.
 *
 * Routing rules:
 *  - from = USER  -> /topic/chat/admin   (admin sees all user messages)
 *  - from = ADMIN -> /topic/chat/user/{userIdentifier}
 *  - from = BOT   -> /topic/chat/user/{userIdentifier}
 */
@Component
public class ChatListener {

    private static final Logger log = LoggerFactory.getLogger(ChatListener.class);
    private final SimpMessagingTemplate simp;

    public ChatListener(SimpMessagingTemplate simp) {
        this.simp = simp;
    }

    @RabbitListener(queues = "${websocket.queues.chatUserOut}")
    public void onChatEvent(ChatMessage msg) {
        if (msg == null) return;

        String from = (msg.getFrom() == null) ? "" : msg.getFrom().trim().toUpperCase();
        String userId = msg.getUserIdentifier();

        final String destination;
        if ("USER".equals(from)) {
            destination = "/topic/chat/admin";
        } else {
            destination = "/topic/chat/user/" + userId;
        }

        log.info("WS forward: from={}, userIdentifier={}, destination={}, text={}", from, userId, destination, msg.getText());
        simp.convertAndSend(destination, msg);
    }
}
