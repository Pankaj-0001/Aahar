package com.aahar.Aahar.controller;


import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user profile operations
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/user/profile
     * Get current user's profile with nutritional targets
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeDTOs.UserProfileResponse> getUserProfile() {
        User currentUser = userService.getCurretUser();
        CodeDTOs.UserProfileResponse profile = userService.getUserProfile(currentUser.getEmail());
        
        return ResponseEntity.ok(profile);
    }
    
    /**
     * PUT /api/user/profile
     * Update user profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeDTOs.UserProfileResponse> updateUserProfile(
            @Valid @RequestBody CodeDTOs.UpdateUserProfileRequest request) {
        
        User currentUser = userService.getCurretUser();
        CodeDTOs.UserProfileResponse updated = userService.updateUserProfile(currentUser.getEmail(), request);
        
        return ResponseEntity.ok(updated);
    }
}
