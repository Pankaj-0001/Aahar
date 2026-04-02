package com.healthtracker.HealthTracker.controller;


import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for food item operations
 */
@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FoodController {
    
    @Autowired
    private FoodService foodService;
    
    /**
     * GET /api/food/search?q=roti
     * Search for food items
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CodeDTOs.FoodItemResponse>> searchFood(
            @RequestParam("q") String query) {
        
        List<CodeDTOs.FoodItemResponse> results = foodService.searchFoodByName(query);
        return ResponseEntity.ok(results);
    }
}
