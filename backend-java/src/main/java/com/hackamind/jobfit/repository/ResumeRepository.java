package com.hackamind.jobfit.repository;

import com.hackamind.jobfit.model.Resume;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Resume> findBySessionSessionIdOrderByCreatedAtDesc(String sessionId);
    long countByUserId(Long userId);
    long countBySessionSessionId(String sessionId);
}
