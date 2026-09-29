package com.disi.backend.service;

import com.disi.backend.config.RabbitMQConfig;
import com.disi.backend.dto.MessageQueueEvent;
import com.disi.backend.entity.*;
import com.disi.backend.repository.*;
import com.disi.backend.requests.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private User resolveUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is blocked");
        }
        return user;
    }

    public MessageResponse sendMessage(String senderEmail, SendMessageRequest request) {
        User sender = resolveUser(senderEmail);
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        if (Boolean.TRUE.equals(receiver.getIsBanned())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot message a blocked user");
        }

        if (!canMessage(sender, receiver)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only message accepted friends or admins");
        }

        MessageQueueEvent event = new MessageQueueEvent(
                sender.getUserId(),
                receiver.getUserId(),
                request.getContent(),
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, RabbitMQConfig.CHAT_ROUTING_KEY, event);
            log.info("Published message to queue: sender={}, receiver={}", sender.getUserId(), receiver.getUserId());
        } catch (Exception e) {
            log.error("Failed to publish message to queue", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Queue unavailable / processing error");
        }

        Message tempMsg = new Message();
        tempMsg.setMessageId(0L);
        tempMsg.setSender(sender);
        tempMsg.setReceiver(receiver);
        tempMsg.setContent(event.getContent());
        tempMsg.setCreatedAt(event.getCreatedAt());
        tempMsg.setIsRead(false);

        return new MessageResponse(tempMsg);
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void processMessageFromQueue(MessageQueueEvent event) {
        log.info("Consuming message from queue: sender={}, receiver={}", event.getSenderId(), event.getReceiverId());
        try {
            User sender = userRepository.findById(event.getSenderId()).orElse(null);
            User receiver = userRepository.findById(event.getReceiverId()).orElse(null);

            if (sender != null && receiver != null) {
                Message msg = new Message();
                msg.setSender(sender);
                msg.setReceiver(receiver);
                msg.setContent(event.getContent());
                msg.setCreatedAt(event.getCreatedAt());
                msg.setIsRead(false);

                //Test DLQ
                //throw new RuntimeException("Eroare simulata pentru a forta mesajul in DLQ!");

                messageRepository.save(msg);
                log.info("Message successfully persisted to database!");
            } else {
                log.warn("Sender or receiver not found. Discarding message.");
            }
        } catch (Exception e) {
            log.error("Error processing message from queue. Retrying...", e);
            throw e;
        }
    }

    public List<MessageResponse> getConversation(String currentEmail, Long otherUserId) {
        User currentUser = resolveUser(currentEmail);
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Message> messages = messageRepository.findConversation(currentUser, otherUser);

        boolean needsSave = false;
        for (Message m : messages) {
            if (m.getReceiver().getUserId().equals(currentUser.getUserId()) && !Boolean.TRUE.equals(m.getIsRead())) {
                m.setIsRead(true);
                needsSave = true;
            }
        }

        if (needsSave) {
            messageRepository.saveAll(messages);
        }

        return messages.stream().map(MessageResponse::new).collect(Collectors.toList());
    }

    public List<ConversationResponse> getConversationsList(String currentEmail) {
        User currentUser = resolveUser(currentEmail);
        List<User> participants = messageRepository.findConversationsForUser(currentUser);

        return participants.stream()
                .filter(u -> !u.getUserId().equals(currentUser.getUserId()))
                .map(ConversationResponse::new)
                .collect(Collectors.toList());
    }

    private boolean canMessage(User u1, User u2) {
        if ("ADMIN".equals(u1.getRole()) || "ADMIN".equals(u2.getRole())) {
            return true;
        }
        Optional<Friendship> f = friendshipRepository.findFriendshipBetweenUsers(u1, u2);

        return f.isPresent() && "ACCEPTED".equals(f.get().getStatus().name());
    }
}