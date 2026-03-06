package com.hackamind.jobfit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
public class ScanResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "fit_score", nullable = false)
    private Integer fitScore;

    @Column(name = "matched_skills", columnDefinition = "TEXT", nullable = false)
    private String matchedSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT", nullable = false)
    private String missingSkills;

    @Column(name = "suggestions", columnDefinition = "TEXT", nullable = false)
    private String suggestions;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    public Integer getFitScore() { return fitScore; }
    public void setFitScore(Integer fitScore) { this.fitScore = fitScore; }
    public String getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; }
    public String getMissingSkills() { return missingSkills; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }
    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
