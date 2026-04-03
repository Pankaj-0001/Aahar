package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationEngine {

    public List<CodeDTOs.Recommendation> generate(
            List<CodeDTOs.DietAnalysis> analysis,
            User.DietGoal goal) {

        List<CodeDTOs.Recommendation> recs = new ArrayList<>();

        for (CodeDTOs.DietAnalysis a : analysis) {
            if ("DEFICIENT".equals(a.getStatus())) {
                recs.addAll(deficiency(a));
            } else if ("EXCESS".equals(a.getStatus())) {
                recs.addAll(excess(a));
            }
        }

        return recs.stream().limit(5).toList();
    }

    private List<CodeDTOs.Recommendation> deficiency(CodeDTOs.DietAnalysis a) {

        List<CodeDTOs.Recommendation> list = new ArrayList<>();

        if ("Protein".equals(a.getNutrient())) {
            list.add(new CodeDTOs.Recommendation("BREAKFAST",
                    "Add eggs", "Eggs", "2", 12.6));
        }

        if ("Fiber".equals(a.getNutrient())) {
            list.add(new CodeDTOs.Recommendation("LUNCH",
                    "Add fruits", "Banana", "1", 2.6));
        }

        return list;
    }

    private List<CodeDTOs.Recommendation> excess(CodeDTOs.DietAnalysis a) {

        List<CodeDTOs.Recommendation> list = new ArrayList<>();

        if ("Carbs".equals(a.getNutrient())) {
            list.add(new CodeDTOs.Recommendation("LUNCH",
                    "Reduce rice", "Rice", "half", 0.0));
        }

        return list;
    }
}