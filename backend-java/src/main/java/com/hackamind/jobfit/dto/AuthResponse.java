package com.hackamind.jobfit.dto;

public class AuthResponse {
    private Long userId;
    private String email;
    private String plan;
    private String message;

    public AuthResponse(Long userId, String email, String plan, String message) {
        this.userId = userId;
        this.email = email;
        this.plan = plan;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPlan() { return plan; }
    public String getMessage() { return message; }
}
