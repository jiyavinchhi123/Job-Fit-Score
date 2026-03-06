package com.hackamind.jobfit.repository;

import com.hackamind.jobfit.model.SessionRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionRecord, String> {
    Optional<SessionRecord> findBySessionId(String sessionId);
}
