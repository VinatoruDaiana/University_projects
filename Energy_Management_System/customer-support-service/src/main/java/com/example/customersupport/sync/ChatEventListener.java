package com.example.customersupport.sync;

import com.example.customersupport.dtos.ChatRequestMessage;
import com.example.customersupport.dtos.ChatResponseMessage;
import com.example.customersupport.services.RuleBasedChatbotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * USER  -> forward to ADMIN + optional BOT reply
 * ADMIN -> forward to USER
 */
@Component
public class ChatEventListener {

    private static final Logger log = LoggerFactory.getLogger(ChatEventListener.class);

    private final RuleBasedChatbotService chatbot;
    private final RabbitTemplate rabbit;
    private final String outQueue;

    public ChatEventListener(RuleBasedChatbotService chatbot,
                             RabbitTemplate chatRabbitTemplate,
                             @Value("${chat.response.queue}") String responseQueue) {
        this.chatbot = chatbot;
        this.rabbit = chatRabbitTemplate;
        this.outQueue = responseQueue;
    }

    @RabbitListener(queues = "${chat.request.queue}")
    public void onChatEvent(ChatRequestMessage msg) {
        if (msg == null) return;

        if (msg.getTimestamp() == null) {
            msg.setTimestamp(System.currentTimeMillis());
        }
        if (msg.getFrom() == null || msg.getFrom().isBlank()) {
            msg.setFrom("USER");
        }

        String from = msg.getFrom().trim().toUpperCase();
        log.info("Inbound chat: from={}, userIdentifier={}, text={}", from, msg.getUserIdentifier(), msg.getText());

        // 1) Forward mesajul mai departe (ws-service îl rutează: USER->admin, ADMIN->user)
        rabbit.convertAndSend(outQueue, msg);

        // 2) Doar USER declanșează chatbot-ul (rules/AI)
        if (!"USER".equals(from)) return;

        try {
            ChatResponseMessage bot = chatbot.handle(msg);
            if (bot == null || bot.getReply() == null || bot.getReply().isBlank()) return;

            ChatRequestMessage botMsg = new ChatRequestMessage();
            botMsg.setChatId(msg.getChatId());
            botMsg.setUserIdentifier(msg.getUserIdentifier());
            botMsg.setFrom("BOT");
            botMsg.setText(bot.getReply());

            Instant ts = (bot.getTimestamp() != null) ? bot.getTimestamp() : Instant.now();
            botMsg.setTimestamp(ts.toEpochMilli());

            rabbit.convertAndSend(outQueue, botMsg);

        } catch (Exception e) {
            // chiar dacă AI pică, mesajul USER a fost deja forwardat către ADMIN
            log.warn("Chatbot failed (message still forwarded to admin). Cause: {}", e.getMessage());
        }
    }
}
