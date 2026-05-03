package com.aahar.Aahar.controller;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Service.DietAnalysisService;
import com.aahar.Aahar.Service.UserService;
import com.aahar.Aahar.Service.WeeklyReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DietController {
    
    @Autowired
    private DietAnalysisService dietAnalysisService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private WeeklyReportService weeklyReportService;
    
    /**
     * POST /api/diet/analyze
     * Create and analyze a diet record
     */
    @PostMapping("/analyze")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeDTOs.DietRecordResponse> analyzeDiet(
            @Valid @RequestBody CodeDTOs.CreateDietRecordRequest request) throws JsonProcessingException {
        
        User curretUser = userService.getCurretUser();
        CodeDTOs.DietRecordResponse response = dietAnalysisService.createDietRecord(curretUser.getEmail(), request);
        
        return ResponseEntity.ok(response);
    }
    /**
     * Put /api/diet/analyze
     * edit and analyze a diet record
     */
//    @PutMapping("analyze")
//    @PreAuthorize("isAuthenticated")
//    public ResponseEntity<CodeDTOs.DietRecordResponse> editDiet(@Valid@RequestBody CodeDTOs.CreateDietRecordRequest request) throws JsonProcessingException{
//        User currentUser = userService.getCurretUser();
//        CodeDTOs.DietRecordResponse response= dietAnalysisService.editDiet(currentUser.getEmail(), request);
//
//        return ResponseEntity.ok(response);
//    }
    
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

    /**
     * GET /api/diet/weekly-report
     * Get a weekly report with ai insight
     */
    @GetMapping("/weekly-report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeDTOs.WeeklyProgressResponse> getWeeklyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        User currentUser = userService.getCurretUser();

        LocalDate end   = endDate   != null ? endDate   : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(6);

        return ResponseEntity.ok(
                weeklyReportService.getWeeklyReport(currentUser.getId(), start, end)
        );
    }
}
