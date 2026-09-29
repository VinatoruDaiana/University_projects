package com.disi.backend.service;

import com.disi.backend.dto.ModerationReportResponse;
import com.disi.backend.entity.ModerationReport;
import com.disi.backend.entity.User;
import com.disi.backend.repository.CommentRepository;
import com.disi.backend.repository.ModerationReportRepository;
import com.disi.backend.repository.PostRepository;
import com.disi.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModerationService {

    private static final Logger log = LoggerFactory.getLogger(ModerationService.class);

    @Autowired
    private ModerationReportRepository moderationReportRepository;

    @Autowired
    private AIModerationService aiModerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    public void analyzeAndReport(String targetType, Long targetId, Long userId, String content) {
        try {
            if (moderationReportRepository.existsByTargetTypeAndTargetIdAndStatus(targetType, targetId, "PENDING")) {
                return;
            }

            AIModerationService.ModerationResult result = aiModerationService.analyze(content);

            if (result.isFlagged()) {
                ModerationReport report = new ModerationReport();
                report.setTargetType(targetType);
                report.setTargetId(targetId);
                report.setUserId(userId);
                report.setFlaggedReason(result.reason());
                report.setFlaggedCategory(result.category());
                report.setConfidenceScore(result.confidence());
                report.setContent(content);
                report.setStatus("PENDING");
                moderationReportRepository.save(report);
                log.info("Moderation report created for {} id={}", targetType, targetId);
            }
        } catch (Exception e) {
            log.error("Failed to analyze content for moderation: {}", e.getMessage());
        }
    }

    public List<ModerationReportResponse> getAllReports() {
        return moderationReportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(r -> ModerationReportResponse.from(r, userRepository.findById(r.getUserId()).orElse(null)))
                .toList();
    }

    public List<ModerationReportResponse> getPendingReports() {
        return moderationReportRepository.findByStatusOrderByCreatedAtDesc("PENDING").stream()
                .map(r -> ModerationReportResponse.from(r, userRepository.findById(r.getUserId()).orElse(null)))
                .toList();
    }

    @Transactional
    public void ignoreReport(Long reportId, String adminEmail) {
        ModerationReport report = getReport(reportId);
        User admin = getAdmin(adminEmail);

        report.setStatus("IGNORED");
        report.setReviewedByAdminId(admin.getUserId());
        report.setReviewedAt(LocalDateTime.now());
        moderationReportRepository.save(report);
        log.info("Report {} ignored by admin {}", reportId, adminEmail);
    }

    @Transactional
    public void warnUser(Long reportId, String adminEmail) {
        ModerationReport report = getReport(reportId);
        User admin = getAdmin(adminEmail);

        User user = userRepository.findById(report.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        deleteContent(report);

        mailService.sendEmail(
                user.getEmail(),
                "Warning: Content Policy Violation",
                "Hi " + user.getUsername() + ",\n\n" +
                "Your recent content was flagged and removed for the following reason:\n\n" +
                report.getFlaggedReason() + "\n\n" +
                "Please review our community guidelines.\n\nThe Pulse Team"
        );

        report.setStatus("WARNED");
        report.setReviewedByAdminId(admin.getUserId());
        report.setReviewedAt(LocalDateTime.now());
        moderationReportRepository.save(report);
        log.info("User {} warned for report {}", user.getUsername(), reportId);
    }

    @Transactional
    public void banUserForReport(Long reportId, String adminEmail) {
        ModerationReport report = getReport(reportId);
        User admin = getAdmin(adminEmail);

        User user = userRepository.findById(report.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        deleteContent(report);

        userService.banUser(user.getUserId(), "Violation of community guidelines: " + report.getFlaggedCategory());

        report.setStatus("BANNED");
        report.setReviewedByAdminId(admin.getUserId());
        report.setReviewedAt(LocalDateTime.now());
        moderationReportRepository.save(report);
        log.info("User {} banned for report {}", user.getUsername(), reportId);
    }

    private void deleteContent(ModerationReport report) {
        try {
            if ("POST".equals(report.getTargetType())) {
                postRepository.findById(report.getTargetId()).ifPresent(postRepository::delete);
            } else if ("COMMENT".equals(report.getTargetType())) {
                commentRepository.findById(report.getTargetId()).ifPresent(commentRepository::delete);
            }
        } catch (Exception e) {
            log.warn("Could not delete content for report {}: {}", report.getReportId(), e.getMessage());
        }
    }

    private ModerationReport getReport(Long reportId) {
        return moderationReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    private User getAdmin(String adminEmail) {
        return userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
    }
}
