package com.hackamind.jobfit.service;

import com.hackamind.jobfit.dto.AnalyzeJobRequest;
import com.hackamind.jobfit.dto.AnalyzeResponse;
import com.hackamind.jobfit.model.JobDescription;
import com.hackamind.jobfit.model.Resume;
import com.hackamind.jobfit.model.ScanResult;
import com.hackamind.jobfit.model.SessionRecord;
import com.hackamind.jobfit.model.User;
import com.hackamind.jobfit.repository.JobDescriptionRepository;
import com.hackamind.jobfit.repository.ResumeRepository;
import com.hackamind.jobfit.repository.ScanResultRepository;
import com.hackamind.jobfit.util.ResumeTextExtractor;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ScanService {
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final ScanResultRepository scanResultRepository;
    private final ResumeTextExtractor resumeTextExtractor;
    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${python.service.url}")
    private String pythonAnalyzeUrl;

    @Value("${app.free.scan.limit:3}")
    private int freeScanLimit;

    public ScanService(ResumeRepository resumeRepository, JobDescriptionRepository jobDescriptionRepository, ScanResultRepository scanResultRepository, ResumeTextExtractor resumeTextExtractor, RestTemplate restTemplate, AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.scanResultRepository = scanResultRepository;
        this.resumeTextExtractor = resumeTextExtractor;
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    public Resume uploadResume(MultipartFile file, String sessionId, Long userId) throws IOException {
        validateFile(file);
        String resumeText = resumeTextExtractor.extractText(file);
        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException("Could not extract resume text");
        }

        User user = requireUser(userId);
        SessionRecord session = authService.ensureSession(sessionId);
        Resume resume = new Resume();
        resume.setResumeText(resumeText);
        resume.setSession(session);
        resume.setUser(user);
        return resumeRepository.save(resume);
    }

    public AnalyzeResponse analyze(AnalyzeJobRequest request, String sessionId, Long userId) {
        SessionRecord session = authService.ensureSession(sessionId);
        User user = requireUser(userId);

        if (isFreeUserAndExceeded(user, session.getSessionId())) {
            throw new IllegalStateException("Free plan limit reached. Upgrade to Pro for unlimited scans.");
        }

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        if (!isOwner(resume, sessionId, userId)) {
            throw new IllegalArgumentException("Resume does not belong to this user/session");
        }

        JobDescription jd = new JobDescription();
        jd.setResume(resume);
        jd.setJobText(request.getJobDescription().trim());
        jobDescriptionRepository.save(jd);

        Map<String, Object> pythonReq = new HashMap<>();
        pythonReq.put("resume_text", resume.getResumeText());
        pythonReq.put("job_description", request.getJobDescription());

        ResponseEntity<Map> pythonResp = restTemplate.postForEntity(
                pythonAnalyzeUrl,
                new HttpEntity<>(pythonReq, defaultJsonHeaders()),
                Map.class
        );
        if (!pythonResp.getStatusCode().is2xxSuccessful() || pythonResp.getBody() == null) {
            throw new IllegalStateException("AI service error");
        }

        Map body = pythonResp.getBody();
        Integer fitScore = ((Number) body.getOrDefault("fit_score", 0)).intValue();
        List<String> matched = toStringList(body.get("matched_skills"));
        List<String> missing = toStringList(body.get("missing_skills"));
        String suggestions = String.valueOf(body.getOrDefault("suggestions", ""));

        ScanResult result = new ScanResult();
        result.setResume(resume);
        result.setFitScore(fitScore);
        result.setMatchedSkills(String.join(",", matched));
        result.setMissingSkills(String.join(",", missing));
        result.setSuggestions(suggestions);
        result = scanResultRepository.save(result);

        return new AnalyzeResponse(result.getId(), fitScore, matched, missing, suggestions);
    }

    public AnalyzeResponse getResult(Long resultId, String sessionId, Long userId) {
        requireUser(userId);
        ScanResult result = scanResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));
        if (!isOwner(result.getResume(), sessionId, userId)) {
            throw new IllegalArgumentException("Result does not belong to this user/session");
        }
        return new AnalyzeResponse(
                result.getId(),
                result.getFitScore(),
                splitCsv(result.getMatchedSkills()),
                splitCsv(result.getMissingSkills()),
                result.getSuggestions()
        );
    }

    public List<AnalyzeResponse> history(String sessionId, Long userId) {
        User user = requireUser(userId);
        List<ScanResult> results = scanResultRepository.findByResumeUserIdOrderByCreatedAtDesc(user.getId());
        return results.stream().map(r -> new AnalyzeResponse(
                r.getId(),
                r.getFitScore(),
                splitCsv(r.getMatchedSkills()),
                splitCsv(r.getMissingSkills()),
                r.getSuggestions()
        )).collect(Collectors.toList());
    }

    private User requireUser(Long userId) {
        User user = authService.getUserById(userId);
        if (user == null) {
            throw new com.hackamind.jobfit.controller.UnauthorizedException("Login required");
        }
        return user;
    }

    private boolean isFreeUserAndExceeded(User user, String sessionId) {
        if (user != null) {
            if (!"free".equalsIgnoreCase(user.getPlan())) return false;
            return scanResultRepository.countByResumeUserId(user.getId()) >= freeScanLimit;
        }
        return scanResultRepository.countByResumeSessionSessionId(sessionId) >= freeScanLimit;
    }

    private boolean isOwner(Resume resume, String sessionId, Long userId) {
        if (userId != null && resume.getUser() != null) {
            return Objects.equals(resume.getUser().getId(), userId);
        }
        return resume.getSession() != null && Objects.equals(resume.getSession().getSessionId(), sessionId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be <= 5MB");
        }
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!(filename.endsWith(".pdf") || filename.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF or DOCX allowed");
        }
    }

    private HttpHeaders defaultJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private List<String> toStringList(Object value) {
        if (!(value instanceof List<?> list)) return List.of();
        return list.stream().map(String::valueOf).collect(Collectors.toList());
    }

    private List<String> splitCsv(String input) {
        if (input == null || input.isBlank()) return List.of();
        return Arrays.stream(input.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
