package com.buddy.auth.controller;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;
import com.buddy.auth.dto.RefreshRequest;
import com.buddy.auth.dto.RegisterRequest;
import com.buddy.auth.dto.RegisterResponse;
import com.buddy.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
            @Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }
}