package com.hackamind.jobfit.controller;

import com.hackamind.jobfit.dto.AuthRequest;
import com.hackamind.jobfit.dto.AuthResponse;
import com.hackamind.jobfit.model.User;
import com.hackamind.jobfit.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest request,
                                 @RequestHeader("X-Session-Id") String sessionId) {
        return authService.register(request, sessionId);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request,
                              @RequestHeader("X-Session-Id") String sessionId) {
        return authService.login(request, sessionId);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        User user = authService.getUserById(userId);
        if (user == null) {
            return Map.of("authenticated", false, "plan", "free");
        }
        return Map.of("authenticated", true, "userId", user.getId(), "email", user.getEmail(), "plan", user.getPlan());
    }
}
