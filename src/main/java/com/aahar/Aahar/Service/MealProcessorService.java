package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.DietRecord;
import com.aahar.Aahar.Entity.FoodItem;
import com.aahar.Aahar.Entity.MealEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        meal.setCreatedAt(LocalDateTime.now());
        meal.setCalories(nutrition.getCalories());
        meal.setProtein(nutrition.getProtein());
        meal.setCarbs(nutrition.getCarbs());
        meal.setFats(nutrition.getFats());
        meal.setFiber(nutrition.getFiber());

        return meal;
    }
}