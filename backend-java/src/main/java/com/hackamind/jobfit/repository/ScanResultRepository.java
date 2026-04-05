package com.hackamind.jobfit.repository;

import com.hackamind.jobfit.model.ScanResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findByResumeUserIdOrderByCreatedAtDesc(Long userId);
    List<ScanResult> findByResumeSessionSessionIdOrderByCreatedAtDesc(String sessionId);
    long countByResumeUserId(Long userId);
    long countByResumeSessionSessionId(String sessionId);
}
