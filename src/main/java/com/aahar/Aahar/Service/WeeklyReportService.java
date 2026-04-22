package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.DietRecord;
import com.aahar.Aahar.Repository.DietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final DietRecordRepository dietRecordRepository;
    private final OllamaChatModel chatModel;

    public CodeDTOs.WeeklyProgressResponse getWeeklyReport(Long userId) {

        LocalDate weekEnd = LocalDate.now();
        LocalDate weekStart = weekEnd.minusDays(6);

        List<DietRecord> records = dietRecordRepository
            .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                userId, weekStart, weekEnd
            );

        // Daily progress list
        List<CodeDTOs.DailyProgress> dailyProgress = records.stream()
            .map(r -> new CodeDTOs.DailyProgress(
                r.getRecordDate(),
                r.getDietScore(),
                new CodeDTOs.NutritionalBreakdown(
                    r.getTotalCalories(),
                    r.getTotalProtein(),
                    r.getTotalCarbs(),
                    r.getTotalFats(),
                    r.getTotalFiber()
                )
            )).toList();

        // Weekly averages
        double avgScore = records.stream()
            .mapToInt(DietRecord::getDietScore)
            .average().orElse(0.0);

        CodeDTOs.NutritionalBreakdown weeklyAvg = new CodeDTOs.NutritionalBreakdown(
            average(records, DietRecord::getTotalCalories),
            average(records, DietRecord::getTotalProtein),
            average(records, DietRecord::getTotalCarbs),
            average(records, DietRecord::getTotalFats),
            average(records, DietRecord::getTotalFiber)
        );

        // AI insights
        String insights = records.isEmpty()
            ? "No data for this week."
            : generateInsights(avgScore, weeklyAvg, records.size());

        return new CodeDTOs.WeeklyProgressResponse(
            weekStart, weekEnd, avgScore, weeklyAvg, dailyProgress, insights
        );
    }

    private double average(List<DietRecord> records,
                           java.util.function.Function<DietRecord, Double> getter) {
        return records.stream()
            .mapToDouble(r -> getter.apply(r) != null ? getter.apply(r) : 0.0)
            .average().orElse(0.0);
    }

    private String generateInsights(double avgScore,
                                    CodeDTOs.NutritionalBreakdown avg,
                                    int days) {
        String prompt = String.format("""
            You are an Indian nutritionist. Give a 2-3 line weekly diet summary.
            Be specific, encouraging, and mention Indian foods where relevant.
            Keep it under 60 words.
            
            Data:
            - Days tracked: %d
            - Average diet score: %.1f/100
            - Avg daily calories: %.0f kcal
            - Avg protein: %.1fg
            - Avg carbs: %.1fg
            - Avg fats: %.1fg
            - Avg fiber: %.1fg
            
            Respond with plain text only, no JSON.
            """,
            days, avgScore,
            avg.getCalories(), avg.getProtein(),
            avg.getCarbs(), avg.getFats(), avg.getFiber()
        );

        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            return "Good effort this week! Keep tracking your meals consistently.";
        }
    }
}