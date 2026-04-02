package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.DietRecord;
import com.healthtracker.HealthTracker.Entity.MealEntry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DietRecordMapper {

    public CodeDTOs.DietRecordResponse toResponse(
            DietRecord record,
            CodeDTOs.NutritionalBreakdown totals,
            List<CodeDTOs.DietAnalysis> analysis,
            List<CodeDTOs.Recommendation> recs) {

        List<CodeDTOs.MealEntryResponse> meals = record.getMeals().stream()
                .map(this::mapMeal)
                .toList();

        CodeDTOs.DietRecordResponse res = new CodeDTOs.DietRecordResponse();
        res.setId(record.getId());
        res.setRecordDate(record.getRecordDate());
        res.setTotals(totals);
        res.setDietScore(record.getDietScore());
        res.setAnalysis(analysis);
        res.setRecommendations(recs);
        res.setMeals(meals);

        return res;
    }

    private CodeDTOs.MealEntryResponse mapMeal(MealEntry m) {

        return new CodeDTOs.MealEntryResponse(
                m.getId(),
                m.getMealType().toString(),
                m.getFoodName(),
                m.getPortionDescription(),
                new CodeDTOs.NutritionalBreakdown(
                        m.getCalories(),
                        m.getProtein(),
                        m.getCarbs(),
                        m.getFats(),
                        m.getFiber()
                )
        );
    }
}