package com.hackamind.jobfit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String plan = "free";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
