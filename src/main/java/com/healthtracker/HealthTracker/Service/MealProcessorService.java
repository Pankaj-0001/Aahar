package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.DietRecord;
import com.healthtracker.HealthTracker.Entity.FoodItem;
import com.healthtracker.HealthTracker.Entity.MealEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealProcessorService {

    private final PortionParserService portionParser;
    private final FoodService foodService;

    public MealEntry process(CodeDTOs.MealEntryRequest request, DietRecord record) {

        double grams = portionParser.parsePortionToGrams(
                request.getFoodName(),
                request.getPortionDescription()
        );

        FoodItem food = foodService.findFoodByName(request.getFoodName());

        CodeDTOs.NutritionalBreakdown nutrition =
                foodService.calculateNutrition(food, grams);

        MealEntry meal = new MealEntry();
        meal.setDietRecord(record);
        meal.setMealType(request.getMealType());
        meal.setFoodName(request.getFoodName());
        meal.setPortionDescription(request.getPortionDescription());
        meal.setQuantity(grams);

        meal.setCalories(nutrition.getCalories());
        meal.setProtein(nutrition.getProtein());
        meal.setCarbs(nutrition.getCarbs());
        meal.setFats(nutrition.getFats());
        meal.setFiber(nutrition.getFiber());

        return meal;
    }
}