package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import org.springframework.stereotype.Service;

@Service
public class NutritionCalculatorService {

    public CodeDTOs.NutritionalTargets caltulateTargets(User user) {

        double bmr = calculateBMR(user);
        double tdee = calculateTdee(bmr, user.getActivityLevel());
        double targetCalories = calculateCaloriesForGoal(tdee, user.getGoal());

        double weight = user.getWeight();

        double proteinPerKg = switch (user.getGoal()) {
            case WEIGHT_LOSS -> 2.0;
            case MUSCLE_BUILDING -> 2.2;
            case WEIGHT_GAIN -> 1.8;
            case MAINTENANCE -> 1.6;
        };

        double proteinGrams = clamp(weight * proteinPerKg, 110, 180);
        double proteinCalories = proteinGrams * 4.0;

        double fatRatio = switch (user.getGoal()) {
            case WEIGHT_LOSS -> 0.25;
            case MUSCLE_BUILDING -> 0.25;
            case WEIGHT_GAIN -> 0.30;
            case MAINTENANCE -> 0.30;
        };

        double fatCalories = targetCalories * fatRatio;
        double fatGrams = fatCalories / 9.0;

        fatGrams = clamp(fatGrams, 45, 90);
        fatCalories = fatGrams * 9.0;

        double remainingCalories = targetCalories - (proteinCalories + fatCalories);

        if (remainingCalories < 0) {
            fatCalories = targetCalories - proteinCalories;
            fatGrams = Math.max(fatCalories / 9.0, 45);
            fatCalories = fatGrams * 9.0;
            remainingCalories = targetCalories - (proteinCalories + fatCalories);
        }

        double carbsGrams = Math.max(remainingCalories / 4.0, 0);

        double fiberFromWeight = weight * 0.5;
        double fiberFromCalories = (targetCalories / 1000.0) * 10.0;

        double fiberGrams = (fiberFromWeight + fiberFromCalories) / 2.0;
        fiberGrams = clamp(fiberGrams, 25, 45);

        return new CodeDTOs.NutritionalTargets(
                 (targetCalories),
                 (proteinGrams),
                 (carbsGrams),
                 (fatGrams),
                 (fiberGrams)
        );
    }

    private double calculateCaloriesForGoal(double tdee, User.DietGoal goal) {
        return switch (goal) {
            case WEIGHT_LOSS -> tdee - 400;
            case WEIGHT_GAIN -> tdee + 300;
            case MUSCLE_BUILDING -> tdee + 250;
            case MAINTENANCE -> tdee;
        };
    }

    private double calculateTdee(double bmr, User.ActivityLevel activityLevel) {
        return bmr * switch (activityLevel) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };
    }

    private double calculateBMR(User user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = user.getAge();

        if (user.getGender() == User.Gender.MALE) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private long round(double value) {
        return Math.round(value);
    }
}