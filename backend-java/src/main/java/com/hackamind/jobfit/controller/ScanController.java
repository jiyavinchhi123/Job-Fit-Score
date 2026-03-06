package com.hackamind.jobfit.controller;

import com.hackamind.jobfit.dto.AnalyzeJobRequest;
import com.hackamind.jobfit.dto.AnalyzeResponse;
import com.hackamind.jobfit.model.Resume;
import com.hackamind.jobfit.service.ScanService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ScanController {
    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/upload-resume")
    public Map<String, Object> uploadResume(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) throws IOException {
        Resume resume = scanService.uploadResume(file, sessionId, userId);
        return Map.of("resumeId", resume.getId(), "message", "Resume uploaded successfully");
    }

    @PostMapping("/analyze-job")
    public AnalyzeResponse analyzeJob(@Valid @RequestBody AnalyzeJobRequest request,
                                      @RequestHeader("X-Session-Id") String sessionId,
                                      @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return scanService.analyze(request, sessionId, userId);
    }

    @GetMapping("/scan-result/{id}")
    public AnalyzeResponse getScanResult(@PathVariable("id") Long id,
                                         @RequestHeader("X-Session-Id") String sessionId,
                                         @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return scanService.getResult(id, sessionId, userId);
    }

    @GetMapping("/dashboard/history")
    public List<AnalyzeResponse> history(@RequestHeader("X-Session-Id") String sessionId,
                                         @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return scanService.history(sessionId, userId);
    }
}
