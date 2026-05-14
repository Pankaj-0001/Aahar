package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationEngine {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CodeDTOs.Recommendation> generate(
            List<CodeDTOs.DietAnalysis> analysis,
            User.DietGoal goal
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            You are generating diet recommendations for an Indian nutrition app.
            
            Your task is to return ONLY a valid JSON array.
            
            Do not write:
            - explanations
            - markdown
            - headings
            - notes
            - intro text
            - extra sentences
            - code blocks
            
            Return exactly 3 recommendation objects.
            
            Each object must follow this exact structure:
            
            [
              {
                "mealType": "BREAKFAST",
                "suggestion": "Increase protein intake",
                "foodItem": "Paneer Cheela",
                "quantity": "2 pieces",
                "proteinGain": 15.0
              }
            ]
            
            Rules:
            - mealType must be BREAKFAST, LUNCH, or DINNER
            - foodItem must be an Indian food
            - quantity must be realistic
            - proteinGain must be a number
            - response must start with [
            - response must end with ]
            - valid JSON only
            
            User Goal:
            """);

        prompt.append(goal).append("\n\n");

        for (CodeDTOs.DietAnalysis item : analysis) {

            prompt.append(item.getNutrient())
                    .append(": ")
                    .append(item.getStatus())
                    .append(" ")
                    .append(String.format("%.1f", item.getPercentageOfTarget()))
                    .append("%\n");
        }

        try {

            String response = chatModel.call(prompt.toString());

            int start = response.indexOf("[");
            int end = response.lastIndexOf("]");

            if (start == -1 || end == -1) {
                return fallback();
            }

            String json = response.substring(start, end + 1);

            return objectMapper.readValue(
                    json,
                    new TypeReference<List<CodeDTOs.Recommendation>>() {}
            );

        } catch (Exception e) {

            return fallback();
        }
    }

    private List<CodeDTOs.Recommendation> fallback() {

        return List.of(

                new CodeDTOs.Recommendation(
                        "BREAKFAST",
                        "Increase protein intake",
                        "Paneer Cheela",
                        "2 pieces",
                        15.0
                ),

                new CodeDTOs.Recommendation(
                        "LUNCH",
                        "Balanced meal",
                        "Dal Rice",
                        "1 plate",
                        10.0
                ),

                new CodeDTOs.Recommendation(
                        "DINNER",
                        "Fiber rich dinner",
                        "Mixed Vegetable Roti",
                        "2 rotis",
                        8.0
                )
        );
    }
}