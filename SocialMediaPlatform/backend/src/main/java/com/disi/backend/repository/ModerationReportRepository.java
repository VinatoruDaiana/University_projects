package com.disi.backend.repository;

import com.disi.backend.entity.ModerationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationReportRepository extends JpaRepository<ModerationReport, Long> {
    List<ModerationReport> findByStatusOrderByCreatedAtDesc(String status);
    List<ModerationReport> findAllByOrderByCreatedAtDesc();
    boolean existsByTargetTypeAndTargetIdAndStatus(String targetType, Long targetId, String status);
}
