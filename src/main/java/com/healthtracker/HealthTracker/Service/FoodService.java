package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.FoodItem;
import com.healthtracker.HealthTracker.Repository.FoodRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodRepo foodRepo;

    public FoodService(FoodRepo foodRepo) {
        this.foodRepo = foodRepo;
    }

    public List<CodeDTOs.FoodItemResponse> searchFoodByName(String query){
        List<FoodItem> foods = foodRepo.findByNameContainingIgnoreCase(query);

        return foods.stream().map(this::makeFoodResponse).collect(Collectors.toList());
    }

    public FoodItem getFoodById(Long id){
        FoodItem foodItem = foodRepo.findByid(id).orElseThrow(()->new RuntimeException("FoodItem not found"));
        return foodItem;
    }

    public FoodItem findFoodByName(String foodName) {
        List<FoodItem> matches = foodRepo.findByNameContainingIgnoreCase(foodName);

        if (!matches.isEmpty()) {

            return matches.get(0);
        }
        return createDefaultFoodItem(foodName);
    }

    private FoodItem createDefaultFoodItem(String foodName) {
        FoodItem defaultFood = new FoodItem();
        defaultFood.setName(foodName);
        defaultFood.setCategory("Unknown");
        defaultFood.setServingSize(0.0);
        defaultFood.setServingUnit("grams");
        defaultFood.setCalories(0.0);
        defaultFood.setProtein(0.0);
        defaultFood.setCarbs(0.0);
        defaultFood.setFats(0.0);
        defaultFood.setFiber(0.0);

        return defaultFood;
    }

    public CodeDTOs.NutritionalBreakdown calculateNutrition(FoodItem food, double gramsConsumed) {
        double servingSize = food.getServingSize();
        double multiplier = gramsConsumed / servingSize;

        CodeDTOs.NutritionalBreakdown nutrition = new CodeDTOs.NutritionalBreakdown();
        nutrition.setCalories(food.getCalories() * multiplier);
        nutrition.setProtein(food.getProtein() * multiplier);
        nutrition.setCarbs(food.getCarbs() * multiplier);
        nutrition.setFats(food.getFats() * multiplier);
        nutrition.setFiber(food.getFiber() * multiplier);

        return nutrition;
    }


    private CodeDTOs.FoodItemResponse makeFoodResponse(FoodItem food) {
        CodeDTOs.NutritionalBreakdown nutrition = new CodeDTOs.NutritionalBreakdown();
        nutrition.setCalories(food.getCalories());
        nutrition.setProtein(food.getProtein());
        nutrition.setCarbs(food.getCarbs());
        nutrition.setFats(food.getFats());
        nutrition.setFiber(food.getFiber());

        CodeDTOs.FoodItemResponse response = new CodeDTOs.FoodItemResponse();
        response.setId(food.getId());
        response.setName(food.getName());
        response.setCategory(food.getCategory());
        response.setServingSize(food.getServingSize());
        response.setServingUnit(food.getServingUnit());
        response.setNutrition(nutrition);

        return response;
    }
}
