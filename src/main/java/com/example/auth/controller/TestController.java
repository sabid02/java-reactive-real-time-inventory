package com.example.auth.controller;

import com.example.auth.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Reactive REST Controller for testing role-based access control.
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * Public endpoint accessible to anyone without JWT authentication.
     * GET /api/v1/test/public
     */
    @GetMapping("/public")
    public Mono<ResponseEntity<ApiResponse<String>>> publicAccess() {
        return Mono.just(ResponseEntity.ok(
                ApiResponse.success("Public Content: Accessible by anyone!", "Public Resource Content")
        ));
    }

    /**
     * Protected endpoint accessible to users with ROLE_USER or ROLE_ADMIN.
     * GET /api/v1/test/user
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<ResponseEntity<ApiResponse<String>>> userAccess() {
        return Mono.just(ResponseEntity.ok(
                ApiResponse.success("User Content: Protected resource for authenticated Users!", "User Secret Resource")
        ));
    }

    /**
     * Protected endpoint accessible exclusively to users with ROLE_ADMIN.
     * GET /api/v1/test/admin
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ApiResponse<String>>> adminAccess() {
        return Mono.just(ResponseEntity.ok(
                ApiResponse.success("Admin Content: High security resource for Administrators only!", "Admin Confidential Resource")
        ));
    }
}
