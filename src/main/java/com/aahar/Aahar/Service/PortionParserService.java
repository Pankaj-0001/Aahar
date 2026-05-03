package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.FoodItem;
import com.aahar.Aahar.Entity.PortionKeyword;
import com.aahar.Aahar.Entity.PortionMapping;
import com.aahar.Aahar.Repository.PortionKeywordRepo;
import com.aahar.Aahar.Repository.PortionMappingRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        Optional<PortionMapping> mapping = portionMappingRepo
                .findFirstByPortionTypeAndPortionSize(type, size);

        double gramsPerUnit = mapping
                .map(PortionMapping::getGramsEquivalent)
                .orElse(getDefaultGrams(type, size));

        return gramsPerUnit * quantity;
    }

    private double getDefaultGrams(PortionMapping.PortionType type, PortionMapping.PortionSize size) {
        return switch (type) {
            case ROTI     -> size == PortionMapping.PortionSize.SMALL ? 25.0 : size == PortionMapping.PortionSize.LARGE ? 50.0 : 35.0;
            case KATORI   -> size == PortionMapping.PortionSize.SMALL ? 80.0 : size == PortionMapping.PortionSize.LARGE ? 160.0 : 120.0;
            case BOWL     -> size == PortionMapping.PortionSize.SMALL ? 100.0 : size == PortionMapping.PortionSize.LARGE ? 200.0 : 150.0;
            case GLASS    -> size == PortionMapping.PortionSize.SMALL ? 150.0 : size == PortionMapping.PortionSize.LARGE ? 300.0 : 200.0;
            case PIECE    -> size == PortionMapping.PortionSize.SMALL ? 50.0 : size == PortionMapping.PortionSize.LARGE ? 150.0 : 100.0;
            case SPOON    -> size == PortionMapping.PortionSize.SMALL ? 5.0 : size == PortionMapping.PortionSize.LARGE ? 20.0 : 14.0;
            case HANDFUL  -> size == PortionMapping.PortionSize.SMALL ? 15.0 : size == PortionMapping.PortionSize.LARGE ? 40.0 : 25.0;
        };
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
