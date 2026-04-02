package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.MealEntry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutritionAggregator {

    public CodeDTOs.NutritionalBreakdown aggregate(List<MealEntry> meals) {

        double calories = 0, protein = 0, carbs = 0, fats = 0, fiber = 0;

        for (MealEntry m : meals) {
            calories += m.getCalories();
            protein += m.getProtein();
            carbs += m.getCarbs();
            fats += m.getFats();
            fiber += m.getFiber();
        }

        return new CodeDTOs.NutritionalBreakdown(
                calories, protein, carbs, fats, fiber
        );
    }
}