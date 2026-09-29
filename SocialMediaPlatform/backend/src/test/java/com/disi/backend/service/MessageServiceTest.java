package com.disi.backend.service;

import com.disi.backend.config.RabbitMQConfig;
import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.Message;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.MessageRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.requests.MessageResponse;
import com.disi.backend.requests.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import com.disi.backend.dto.MessageQueueEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setUserId(1L);
        sender.setEmail("sender@test.com");
        sender.setUsername("sender");
        sender.setRole("USER");
        sender.setIsBanned(false);

        receiver = new User();
        receiver.setUserId(2L);
        receiver.setEmail("receiver@test.com");
        receiver.setUsername("receiver");
        receiver.setRole("USER");
        receiver.setIsBanned(false);
    }

    @Test
    void sendMessage_BetweenFriends_PublishesToQueue() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        Friendship friendship = new Friendship();
        friendship.setRequester(sender);
        friendship.setAddressee(receiver);
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.findFriendshipBetweenUsers(sender, receiver)).thenReturn(Optional.of(friendship));

        MessageResponse response = messageService.sendMessage("sender@test.com", request);

        assertNotNull(response);
        assertEquals(1L, response.getSenderId());
        assertEquals(2L, response.getReceiverId());
        assertEquals("Hello!", response.getContent());
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.CHAT_EXCHANGE), eq(RabbitMQConfig.CHAT_ROUTING_KEY), any(MessageQueueEvent.class));
    }

    @Test
    void sendMessage_SenderNotFound_Throws401() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("unknown@test.com", request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void sendMessage_SenderBanned_Throws403() {
        sender.setIsBanned(true);
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("sender@test.com", request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void sendMessage_ReceiverNotFound_Throws404() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(999L);
        request.setContent("Hello!");

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("sender@test.com", request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void sendMessage_ReceiverBanned_Throws403() {
        receiver.setIsBanned(true);
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("sender@test.com", request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void sendMessage_NotFriends_Throws403() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.findFriendshipBetweenUsers(sender, receiver)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("sender@test.com", request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void sendMessage_AdminCanMessageAnyone() {
        sender.setRole("ADMIN");
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Admin message");

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        MessageResponse response = messageService.sendMessage("sender@test.com", request);

        assertNotNull(response);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.CHAT_EXCHANGE), eq(RabbitMQConfig.CHAT_ROUTING_KEY), any(MessageQueueEvent.class));
    }

    @Test
    void sendMessage_QueueUnavailable_Throws500() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setContent("Hello!");

        Friendship friendship = new Friendship();
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.findFriendshipBetweenUsers(sender, receiver)).thenReturn(Optional.of(friendship));
        doThrow(new RuntimeException("Connection refused")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(MessageQueueEvent.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.sendMessage("sender@test.com", request));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void getConversation_ReturnsMessages() {
        Message msg = new Message();
        msg.setMessageId(1L);
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent("Hi");
        msg.setCreatedAt(LocalDateTime.now());
        msg.setIsRead(false);

        when(userRepository.findByEmail("receiver@test.com")).thenReturn(Optional.of(receiver));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.findConversation(receiver, sender)).thenReturn(List.of(msg));

        List<MessageResponse> conversation = messageService.getConversation("receiver@test.com", 1L);

        assertEquals(1, conversation.size());
        assertTrue(msg.getIsRead());
        verify(messageRepository).saveAll(any());
    }

    @Test
    void getConversation_UserNotFound_Throws404() {
        when(userRepository.findByEmail("receiver@test.com")).thenReturn(Optional.of(receiver));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> messageService.getConversation("receiver@test.com", 999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
