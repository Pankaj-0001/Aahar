package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.FoodItem;
import com.healthtracker.HealthTracker.Entity.PortionKeyword;
import com.healthtracker.HealthTracker.Entity.PortionMapping;
import com.healthtracker.HealthTracker.Repository.PortionKeywordRepo;
import com.healthtracker.HealthTracker.Repository.PortionMappingRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PortionParserService {

    private final PortionKeywordRepo keywordRepo;
    private final FoodService foodService;
    private final PortionMappingRepo portionMappingRepo;

    public PortionParserService(PortionKeywordRepo keywordRepo, FoodService foodService, PortionMappingRepo portionMappingRepo) {
        this.keywordRepo = keywordRepo;
        this.foodService = foodService;
        this.portionMappingRepo = portionMappingRepo;
    }

    public double parsePortionToGrams(String foodName, String portionDescription) {
        FoodItem foodItem = foodService.findFoodByName(foodName);
        int quantity = extractQuantity(portionDescription);
        CodeDTOs.PortionInfo portionInfo = extractPortionInfo(foodItem, portionDescription);
        return calculateGrams(foodItem,portionInfo.getType(),portionInfo.getSize(),quantity);
    }

    public double calculateGrams(FoodItem foodItem,
                                 PortionMapping.PortionType type,
                                 PortionMapping.PortionSize size,
                                 int quantity) {

        PortionMapping mapping = portionMappingRepo
                .findByFoodItemAndPortionTypeAndPortionSize(foodItem, type, size)
                .orElseThrow(() -> new RuntimeException(
                        "No portion mapping found for: " + type + " " + size));

        return mapping.getGramsEquivalent() * quantity;
    }

    public CodeDTOs.PortionInfo extractPortionInfo(FoodItem foodItem, String description) {

        Set<String> tokens = tokenize(description);

        List<PortionKeyword> matches = keywordRepo.findByKeywordIn(tokens);

        PortionMapping.PortionType type = null;
        PortionMapping.PortionSize size = null;
        for (PortionKeyword k : matches) {
            if (k.getType() != null) {
                type = k.getType();
            }
            if (k.getSize() != null) {
                size = k.getSize();
            }
        }
        if (type == null) {
            type = PortionMapping.PortionType.PIECE;
        }

        if (size == null) {
            size = PortionMapping.PortionSize.MEDIUM;
        }

        return new CodeDTOs.PortionInfo(type, size);
    }

    private int extractQuantity(String description) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 1;
    }
    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
