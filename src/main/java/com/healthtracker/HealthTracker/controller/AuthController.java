package com.healthtracker.HealthTracker.controller;


import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * POST /api/auth/login
     * Authenticate user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody CodeDTOs.LoginRequest loginRequest) {
        CodeDTOs.AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/auth/register
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CodeDTOs.RegisterRequest registerRequest) {
        CodeDTOs.MessageResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}
