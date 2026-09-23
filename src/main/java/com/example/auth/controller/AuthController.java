package com.example.auth.controller;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Reactive REST Controller for User Authentication & Registration endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user account.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<AuthResponse>>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(authResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.success("User registered successfully!", authResponse)));
    }

    /**
     * Authenticates an existing user and returns JWT token.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<AuthResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(authResponse -> ResponseEntity
                        .ok(ApiResponse.success("User logged in successfully!", authResponse)));
    }
}
