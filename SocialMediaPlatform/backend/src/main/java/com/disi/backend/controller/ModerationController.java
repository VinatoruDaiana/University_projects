package com.disi.backend.controller;

import com.disi.backend.dto.ModerationReportResponse;
import com.disi.backend.service.ModerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/moderation")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    @GetMapping("/reports")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<List<ModerationReportResponse>> getAllReports() {
        return ResponseEntity.ok(moderationService.getAllReports());
    }

    @GetMapping("/reports/pending")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<List<ModerationReportResponse>> getPendingReports() {
        return ResponseEntity.ok(moderationService.getPendingReports());
    }

    @PatchMapping("/reports/{reportId}/ignore")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Map<String, String>> ignoreReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        moderationService.ignoreReport(reportId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Report ignored successfully"));
    }

    @PatchMapping("/reports/{reportId}/warn")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Map<String, String>> warnUser(
            @PathVariable Long reportId,
            Authentication authentication) {
        moderationService.warnUser(reportId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "User warned and content removed"));
    }

    @PatchMapping("/reports/{reportId}/ban")
    @PreAuthorize("@authorizationService.isAdmin(authentication.name)")
    public ResponseEntity<Map<String, String>> banUser(
            @PathVariable Long reportId,
            Authentication authentication) {
        moderationService.banUserForReport(reportId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "User banned and content removed"));
    }
}
