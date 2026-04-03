package com.aahar.Aahar.DTO;

import com.aahar.Aahar.Entity.MealEntry;
import com.aahar.Aahar.Entity.PortionMapping;
import com.aahar.Aahar.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class CodeDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    // Register Request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotNull(message = "Age is required")
        private Integer age;

        @NotNull(message = "Gender is required")
        private User.Gender gender;

        @NotNull(message = "Height is required")
        private Double height;

        @NotNull(message = "Weight is required")
        private Double weight;

        @NotNull(message = "Activity level is required")
        private User.ActivityLevel activityLevel;

        @NotNull(message = "Diet goal is required")
        private User.DietGoal goal;
    }

    // Auth Response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;

        public AuthResponse(String token, Long id, String name, String email) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    // Message Response (for generic messages)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }

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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodItemResponse {
        private Long id;
        private String name;
        private String category;
        private Double servingSize;
        private String servingUnit;
        private NutritionalBreakdown nutrition;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalBreakdown {
        private Double calories;
        private Double protein;
        private Double carbs;
        private Double fats;
        private Double fiber;
    }

    @Data
    @AllArgsConstructor
    public static class PortionInfo {
        private PortionMapping.PortionType type;
        private PortionMapping.PortionSize size;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDietRecordRequest {
        private LocalDate recordDate; // Optional, defaults to today

        @NotNull()
        private List<MealEntryRequest> meals;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealEntryRequest {
        @NotNull()
        private MealEntry.MealType mealType;

        @NotNull()
        private String foodName;

        @NotNull()
        private String portionDescription;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DietAnalysis {
        private String nutrient;
        private Double current;
        private Double recommended;
        private String status;
        private Double percentageOfTarget;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String mealType;
        private String suggestion;
        private String foodItem;
        private String quantity;
        private Double proteinGain;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DietRecordResponse {
        private Long id;
        private LocalDate recordDate;
        private NutritionalBreakdown totals;
        private Integer dietScore;
        private List<DietAnalysis> analysis;
        private List<Recommendation> recommendations;
        private List<MealEntryResponse> meals;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyProgressResponse {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Double averageDietScore;
        private NutritionalBreakdown weeklyAverage;
        private List<DailyProgress> dailyProgress;
        private String insights;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private LocalDate date;
        private Integer dietScore;
        private NutritionalBreakdown nutrition;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealEntryResponse {
        private Long id;
        private String mealType;
        private String foodName;
        private String portionDescription;
        private NutritionalBreakdown nutrition;
    }



}
