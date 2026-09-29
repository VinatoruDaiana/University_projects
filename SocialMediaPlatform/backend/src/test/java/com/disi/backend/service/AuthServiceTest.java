package com.disi.backend.service;

import com.disi.backend.requests.LoginRequest;
import com.disi.backend.requests.LoginResponse;
import com.disi.backend.entity.User;
import com.disi.backend.entity.UserProfile;
import com.disi.backend.repository.UserProfileRepository;
import com.disi.backend.repository.UserRepository;
import com.disi.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$12$yeAtRdDjGXpFHHNjfH5gYeMB0qCDtuGdpLiea3o3S4l8Fl94CDUJ2");
        testUser.setIsBanned(false);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void loginUser_SuccessfulAuthentication_ReturnsJwtToken() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getEmail())).thenReturn("mocked_jwt_token");

        // Act
        LoginResponse response = authService.loginUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        assertEquals("1", response.getUser().getId());
        assertEquals("test@example.com", response.getUser().getEmail());

        verify(jwtUtil, times(1)).generateToken(anyString());
    }

    @Test
    void loginUser_UserNotFound_Throws401Unauthorized() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginUser(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid credentials", exception.getReason());
    }

    @Test
    void loginUser_WrongPassword_Throws401Unauthorized() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginUser(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid credentials", exception.getReason());
    }

    @Test
    void loginUser_UserIsBanned_Throws403Forbidden() {
        // Arrange
        testUser.setIsBanned(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginUser(loginRequest));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("User account is blocked", exception.getReason());
    }
}