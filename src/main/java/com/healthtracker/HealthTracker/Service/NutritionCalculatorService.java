package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.User;
import org.springframework.stereotype.Service;

@Service
public class NutritionCalculatorService {

    public CodeDTOs.NutritionalTargets caltulateTargets(User user){
        double bmr = calculateBMR(user);
        double tdee = calculateTdee(bmr,user.getActivityLevel());

        double targetCalories = calculateCaloriesForGoal(tdee,user.getGoal());

        double proteinGram = calculateProtienTarget(user);
        double fatsGrams = calculateFatTarget(user);
        double carbsGrams = calculateCarbTarget(targetCalories, proteinGram, fatsGrams);
        if (carbsGrams < 0) {
            carbsGrams = 0;
        }
        double fiberGrams = calculateFiberTarget(targetCalories);

        CodeDTOs.NutritionalTargets targets = new CodeDTOs.NutritionalTargets(targetCalories,
                proteinGram,
                fatsGrams,
                carbsGrams,
                fiberGrams);

        return targets;
    }

    private double calculateFiberTarget(double calories) {
        return (calories / 1000.0) * 14.0;
    }

    private double calculateFatTarget(User user) {
        double weight = user.getWeight();

        double factor = switch (user.getGoal()) {
            case WEIGHT_LOSS -> 0.7;
            case MUSCLE_BUILDING -> 0.8;
            case WEIGHT_GAIN -> 0.9;
            case MAINTENANCE -> 0.8;
        };

        return weight * factor;
    }

    private double calculateCarbTarget(double calories, double protein, double fat) {
        double remainingCalories = calories - (protein * 4 + fat * 9);
        if (remainingCalories < 0) {
            remainingCalories = 0;
        }

        return remainingCalories / 4;
    }

    private double calculateProtienTarget(User user) {

        double weight = user.getWeight();
        double height = user.getHeight();
        User.DietGoal goal = user.getGoal();

        double bodyFat = estimateBodyFat(weight, height);

        double leanMass = weight * (1 - bodyFat);

        double gramsPerKgLBM = switch (goal) {
            case WEIGHT_LOSS -> 2.2;
            case MUSCLE_BUILDING -> 2.4;
            case WEIGHT_GAIN -> 2.0;
            case MAINTENANCE -> 1.8;
        };
        return leanMass * gramsPerKgLBM;

    }
    private double estimateBodyFat(double weight, double height) {
        double bmi = weight / Math.pow(height / 100.0, 2);
        if (bmi < 18.5) return 0.10;
        if (bmi < 25) return 0.18;
        if (bmi < 30) return 0.25;
        return 0.30;
    }

    private double calculateCaloriesForGoal(double tdee, User.DietGoal goal) {
        return switch (goal) {
            case WEIGHT_LOSS -> tdee - 500;
            case WEIGHT_GAIN -> tdee + 500;
            case MUSCLE_BUILDING -> tdee + 300;
            case MAINTENANCE -> tdee;
        };
    }

    private double calculateTdee(double bmr, User.ActivityLevel activityLevel) {
        double multiplier = switch (activityLevel) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };

        return bmr * multiplier;
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

}
