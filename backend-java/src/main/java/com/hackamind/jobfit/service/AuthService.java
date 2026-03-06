package com.hackamind.jobfit.service;

import com.hackamind.jobfit.dto.AuthRequest;
import com.hackamind.jobfit.dto.AuthResponse;
import com.hackamind.jobfit.model.Resume;
import com.hackamind.jobfit.model.SessionRecord;
import com.hackamind.jobfit.model.User;
import com.hackamind.jobfit.repository.ResumeRepository;
import com.hackamind.jobfit.repository.SessionRepository;
import com.hackamind.jobfit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, ResumeRepository resumeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.resumeRepository = resumeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(AuthRequest request, String sessionId) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPlan("free");
        user = userRepository.save(user);

        attachSessionToUser(sessionId, user);
        mergeAnonymousData(sessionId, user);
        return new AuthResponse(user.getId(), user.getEmail(), user.getPlan(), "Registered successfully");
    }

    @Transactional
    public AuthResponse login(AuthRequest request, String sessionId) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        attachSessionToUser(sessionId, user);
        mergeAnonymousData(sessionId, user);
        return new AuthResponse(user.getId(), user.getEmail(), user.getPlan(), "Login successful");
    }

    public SessionRecord ensureSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("X-Session-Id header is required");
        }
        return sessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    SessionRecord session = new SessionRecord();
                    session.setSessionId(sessionId);
                    return sessionRepository.save(session);
                });
    }

    public User getUserById(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }

    private void attachSessionToUser(String sessionId, User user) {
        SessionRecord session = ensureSession(sessionId);
        session.setUser(user);
        sessionRepository.save(session);
    }

    private void mergeAnonymousData(String sessionId, User user) {
        if (sessionId == null || sessionId.isBlank()) return;
        List<Resume> resumes = resumeRepository.findBySessionSessionIdOrderByCreatedAtDesc(sessionId);
        for (Resume resume : resumes) {
            if (resume.getUser() == null) {
                resume.setUser(user);
            }
        }
        resumeRepository.saveAll(resumes);
    }
}
