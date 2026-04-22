package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationEngine {

    private final OllamaChatModel chatModel;


    public List<CodeDTOs.Recommendation> generate(
            List<CodeDTOs.DietAnalysis> analysis,
            User.DietGoal goal) {

        String prompt = buildPrompt(analysis, goal);

        String response = chatModel.call(prompt);

        return parseResponse(response);
    }

    private String buildPrompt(List<CodeDTOs.DietAnalysis> analysis, User.DietGoal goal) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a professional Indian nutritionist. ");
        sb.append("Based on the following diet analysis, give exactly 3-5 meal recommendations. ");
        sb.append("Use only common Indian foods. ");
        sb.append("Respond ONLY in this exact JSON array format, no extra text:\n");
        sb.append("[{\"mealType\":\"BREAKFAST\",\"suggestion\":\"...\",\"foodItem\":\"...\",\"quantity\":\"...\",\"proteinGain\":0.0}]\n\n");
        sb.append("User Goal: ").append(goal).append("\n");
        sb.append("Diet Analysis:\n");
        for (CodeDTOs.DietAnalysis a : analysis) {
            sb.append("- ").append(a.getNutrient())
                    .append(": ").append(a.getStatus())
                    .append(" (").append(String.format("%.1f", a.getPercentageOfTarget()))
                    .append("% of target)\n");
        }
        return sb.toString();
    }

    private List<CodeDTOs.Recommendation> parseResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Clean response in case model adds extra text
            String json = response.substring(
                    response.indexOf('['), response.lastIndexOf(']') + 1
            );
            return mapper.readValue(json,
                    new TypeReference<List<CodeDTOs.Recommendation>>() {});
        } catch (Exception e) {
            // Fallback — agar LLM ne kuch ajeeb diya
            return List.of(new CodeDTOs.Recommendation(
                    "LUNCH", "Balanced meal recommended", "Dal Chawal", "1 plate", 8.0
            ));
        }
    }
}