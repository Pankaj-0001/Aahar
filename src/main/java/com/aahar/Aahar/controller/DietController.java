package com.aahar.Aahar.controller;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Service.DietAnalysisService;
import com.aahar.Aahar.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for diet analysis operations
 */
@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DietController {
    
    @Autowired
    private DietAnalysisService dietAnalysisService;
    
    @Autowired
    private UserService userService;
    
    /**
     * POST /api/diet/analyze
     * Create and analyze a diet record
     */
    @PostMapping("/analyze")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeDTOs.DietRecordResponse> analyzeDiet(
            @Valid @RequestBody CodeDTOs.CreateDietRecordRequest request) {
        
        User currentUser = userService.getCurretUser();
        CodeDTOs.DietRecordResponse response = dietAnalysisService.createDietRecord(currentUser.getEmail(), request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/diet/records
     * Get all diet records for current user
     */
    @GetMapping("/records")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CodeDTOs.DietRecordResponse>> getUserDietRecords() {
        User currentUser = userService.getCurretUser();
        List<CodeDTOs.DietRecordResponse> records = dietAnalysisService.getUserDietRecords(currentUser.getId());
        
        return ResponseEntity.ok(records);
    }
}
