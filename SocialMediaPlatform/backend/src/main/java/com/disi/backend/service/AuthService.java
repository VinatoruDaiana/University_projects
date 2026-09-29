package com.disi.backend.service;

import com.disi.backend.entity.PasswordResetToken;
import com.disi.backend.repository.PasswordResetTokenRepository;
import com.disi.backend.requests.PasswordResetConfirm;
import com.disi.backend.requests.PasswordResetRequest;
import com.disi.backend.requests.RegisterRequest;
import com.disi.backend.entity.User;
import com.disi.backend.entity.UserProfile;
import com.disi.backend.repository.UserProfileRepository;
import com.disi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.disi.backend.requests.LoginRequest;
import com.disi.backend.requests.LoginResponse;
import com.disi.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${frontend.url:http://localhost}")
    private String frontendUrl;

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mail already in use");
        }

        // save User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");
        User savedUser = userRepository.save(user);

        // create empty profile with the same user id
        UserProfile profile = new UserProfile();
        profile.setUserId(savedUser.getUserId());
        userProfileRepository.save(profile);

        mailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());

        return savedUser;
    }

    public LoginResponse loginUser(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // 2. Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // 3. Check if user is active/banned
        if (user.getIsBanned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is blocked");
        }

        // 4. Generate JWT Token
        String token = jwtUtil.generateToken(user.getEmail());

        // 5. Fetch profile data
        UserProfile profile = userProfileRepository
                .findByUserId(user.getUserId())
                .orElse(new UserProfile());

        // 6. Return Response
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getUserId().toString(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBio(),
                profile.getBirthDate() != null ? profile.getBirthDate().toString() : null,
                profile.getLocation(),
                profile.getProfilePhotoUrl(),
                user.getStatus(),
                user.getIsBanned()
        );

        return new LoginResponse(token, userInfo);
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUserId(user.getUserId());
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            resetToken.setUsed(false);
            passwordResetTokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            mailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
        });
    }

    @Transactional
    public void confirmPasswordReset(String token, PasswordResetConfirm request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        if (resetToken.getUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}