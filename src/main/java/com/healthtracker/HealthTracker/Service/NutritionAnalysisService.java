package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutritionAnalysisService {

    public List<CodeDTOs.DietAnalysis> analyze(
            CodeDTOs.NutritionalBreakdown totals,
            CodeDTOs.NutritionalTargets targets) {

        return List.of(
                analyzeOne("Calories", totals.getCalories(), targets.getDailyCalories(), 10),
                analyzeOne("Protein", totals.getProtein(), targets.getProteinGrams(), 10),
                analyzeOne("Carbs", totals.getCarbs(), targets.getCarbsGrams(), 15),
                analyzeOne("Fats", totals.getFats(), targets.getFatsGrams(), 15),
                analyzeOne("Fiber", totals.getFiber(), targets.getFiberGrams(), 10)
        );
    }

    private CodeDTOs.DietAnalysis analyzeOne(
            String name, double current, double target, double tolerance) {

        double percent = (current / target) * 100;

        String status;
        if (percent < (100 - tolerance)) status = "DEFICIENT";
        else if (percent > (100 + tolerance)) status = "EXCESS";
        else status = "ADEQUATE";

        return new CodeDTOs.DietAnalysis(name, current, target, status, percent);
    }
}