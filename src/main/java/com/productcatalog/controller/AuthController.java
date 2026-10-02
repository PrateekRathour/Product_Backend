package com.productcatalog.controller;

import com.productcatalog.dto.ApiResponse;
import com.productcatalog.dto.auth.JwtResponse;
import com.productcatalog.dto.auth.LoginRequest;
import com.productcatalog.dto.auth.RegisterRequest;
import com.productcatalog.dto.auth.UserInfoResponse;
import com.productcatalog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user login, registration, and profile retrieval")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("User logged in successfully", jwtResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user account")
    public ResponseEntity<ApiResponse<UserInfoResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserInfoResponse userInfo = authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userInfo));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("User is not authenticated"));
        }
        UserInfoResponse userInfo = authService.getCurrentUserInfo(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
