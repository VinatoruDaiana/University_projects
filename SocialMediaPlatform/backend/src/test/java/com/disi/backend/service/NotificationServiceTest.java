package com.disi.backend.service;

import com.disi.backend.entity.Notification;
import com.disi.backend.entity.User;
import com.disi.backend.repository.NotificationRepository;
import com.disi.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("user@test.com");
        testUser.setUsername("testuser");

        testNotification = new Notification();
        testNotification.setNotificationId(200L);
        testNotification.setUserId(1L);
        testNotification.setSenderId(2L);
        testNotification.setType("NEW_POST");
        testNotification.setContent("testuser created a new post");
        testNotification.setRead(false);
        testNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getNotifications_ReturnsUserNotifications() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(testNotification));

        List<Notification> notifications = notificationService.getNotifications("user@test.com");

        assertEquals(1, notifications.size());
        assertEquals(200L, notifications.get(0).getNotificationId());
    }

    @Test
    void getNotifications_UserNotFound_Throws404() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> notificationService.getNotifications("unknown@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getUnreadCount_ReturnsCount() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(3L);

        long count = notificationService.getUnreadCount("user@test.com");

        assertEquals(3, count);
    }

    @Test
    void getUnreadCount_UserNotFound_Throws404() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> notificationService.getUnreadCount("unknown@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void markAsRead_OwnNotification_MarksRead() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findById(200L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);

        Notification result = notificationService.markAsRead(200L, "user@test.com");

        assertTrue(result.isRead());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void markAsRead_OtherUsersNotification_Throws403() {
        User otherUser = new User();
        otherUser.setUserId(99L);
        otherUser.setEmail("other@test.com");

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));
        when(notificationRepository.findById(200L)).thenReturn(Optional.of(testNotification));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> notificationService.markAsRead(200L, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void markAsRead_NotificationNotFound_Throws404() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> notificationService.markAsRead(999L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void markAsRead_UserNotFound_Throws404() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> notificationService.markAsRead(200L, "unknown@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
