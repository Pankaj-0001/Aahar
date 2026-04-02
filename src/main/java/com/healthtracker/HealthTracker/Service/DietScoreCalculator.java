package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DietScoreCalculator {

    public int calculate(List<CodeDTOs.DietAnalysis> analysis) {

        int[] weights = {20, 30, 15, 15, 20};
        double total = 0;

        for (int i = 0; i < analysis.size(); i++) {
            double p = analysis.get(i).getPercentageOfTarget();

            double score;
            if (p >= 90 && p <= 110) score = 100;
            else if (p >= 80 && p <= 120) score = 80;
            else if (p >= 70 && p <= 130) score = 60;
            else score = 40;

            total += (score * weights[i]) / 100.0;
        }

        return (int) Math.round(total);
    }
}