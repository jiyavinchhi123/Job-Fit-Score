package com.hackamind.jobfit.dto;

import java.util.List;

public class AnalyzeResponse {
    private Long resultId;
    private Integer fitScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String suggestions;

    public AnalyzeResponse(Long resultId, Integer fitScore, List<String> matchedSkills, List<String> missingSkills, String suggestions) {
        this.resultId = resultId;
        this.fitScore = fitScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
    }

    public Long getResultId() { return resultId; }
    public Integer getFitScore() { return fitScore; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public String getSuggestions() { return suggestions; }
}
