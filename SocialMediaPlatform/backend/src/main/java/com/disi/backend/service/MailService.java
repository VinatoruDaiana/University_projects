package com.disi.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.sender.address}")
    private String senderAddress;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        String subject = "Welcome to Pulse!";
        String body = "Hi " + username + ",\n\nWelcome to Pulse! Your account has been created successfully.\n\nEnjoy the platform!\n\nThe Pulse Team";
        sendEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String username, String resetLink) {
        String subject = "Password Reset Request";
        String body = "Hi " + username + ",\n\nYou requested a password reset. Click the link below to set a new password:\n\n" + resetLink + "\n\nThis link expires in 30 minutes. If you did not request this, please ignore this email.\n\nThe Pulse Team";
        sendEmail(to, subject, body);
    }

    public void sendBanNotification(String to, String username, String reason) {
        String subject = "Your account has been suspended";
        String body = "Hi " + username + ",\n\nYour account has been suspended.\n\nReason: " + reason + "\n\nIf you believe this is a mistake, please contact support.\n\nThe Pulse Team";
        sendEmail(to, subject, body);
    }

    public void sendUnbanNotification(String to, String username) {
        String subject = "Your account has been reinstated";
        String body = "Hi " + username + ",\n\nYour account suspension has been lifted. You can now log in and use the platform again.\n\nThe Pulse Team";
        sendEmail(to, subject, body);
    }

    public void sendValidationEmail(String to, String username, String validationToken) {
        String subject = "Validate your Pulse account";
        String body = "Hi " + username + ",\n\nPlease validate your account using the following token:\n\n" + validationToken + "\n\nThe Pulse Team";
        sendEmail(to, subject, body);
    }
}
