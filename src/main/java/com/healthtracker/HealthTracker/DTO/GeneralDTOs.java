package com.healthtracker.HealthTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class GeneralDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalTargets {
        private Double dailyCalories;
        private Double proteinGrams;
        private Double carbsGrams;
        private Double fatsGrams;
        private Double fiberGrams;
    }
}
