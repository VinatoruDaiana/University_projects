package com.disi.backend.dto;

import com.disi.backend.entity.ModerationReport;
import com.disi.backend.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ModerationReportResponse {

    private String reportId;
    private String targetType;
    private Long postId;
    private String contentPreview;
    private String flaggedReason;
    private String flaggedCategory;
    private String aiExplanation;
    private Double aiConfidence;
    private String status;
    private ReportedUser reportedUser;
    private Long reviewedByAdminId;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class ReportedUser {
        private String id;
        private String username;
        private String email;
        private String status;
    }

    public static ModerationReportResponse from(ModerationReport report, User user) {
        ModerationReportResponse dto = new ModerationReportResponse();
        dto.setReportId(String.valueOf(report.getReportId()));
        dto.setTargetType(report.getTargetType());
        dto.setPostId(report.getTargetId());
        dto.setContentPreview(report.getContent() != null && report.getContent().length() > 200
                ? report.getContent().substring(0, 200) + "..."
                : report.getContent());
        dto.setFlaggedReason(report.getFlaggedReason());
        dto.setFlaggedCategory(report.getFlaggedCategory());
        dto.setAiExplanation(report.getFlaggedReason());
        dto.setAiConfidence(report.getConfidenceScore());
        dto.setStatus(report.getStatus());
        dto.setReviewedByAdminId(report.getReviewedByAdminId());
        dto.setReviewedAt(report.getReviewedAt());
        dto.setCreatedAt(report.getCreatedAt());

        if (user != null) {
            ReportedUser reportedUser = new ReportedUser();
            reportedUser.setId(String.valueOf(user.getUserId()));
            reportedUser.setUsername(user.getUsername());
            reportedUser.setEmail(user.getEmail());
            reportedUser.setStatus(Boolean.TRUE.equals(user.getIsBanned()) ? "BLOCKED" : "ACTIVE");
            dto.setReportedUser(reportedUser);
        }

        return dto;
    }
}
