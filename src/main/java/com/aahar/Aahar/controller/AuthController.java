package com.aahar.Aahar.controller;


import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Service.AuthService;
import com.aahar.Aahar.Service.GoogleAuthService;
import com.aahar.Aahar.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    @Autowired
    private GoogleAuthService googleAuthService;
    
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



    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody CodeDTOs.GoogleAuthRequest request) {
        try {
            CodeDTOs.AuthResponse response = googleAuthService.handleGoogleLogin(request.getIdToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CodeDTOs.MessageResponse("Google authentication failed: " + e.getMessage()));
        }
    }

    @PutMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(
            @RequestBody CodeDTOs.CompleteProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.completeUserProfile(userDetails.getUsername(), request);

        return ResponseEntity.ok(new CodeDTOs.MessageResponse("Profile completed successfully!"));
    }
}
