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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserProfileRequest {
        private String name;
        private Integer age;
        private Double height;
        private Double weight;
        private String activityLevel;
        private String goal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        private String gender;
        private Double height;
        private Double weight;
        private String activityLevel;
        private String goal;
        private NutritionalTargets targets;
    }
}
