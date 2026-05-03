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

    public CodeDTOs.WeeklyProgressResponse getWeeklyReport(Long userId, LocalDate weekStart, LocalDate weekEnd) {

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
                                        You are an expert nutritionist.
                                        
                                        Generate a detailed weekly diet report in markdown.
                                        
                                        Use this structure:
                                        
                                        ## Weekly Overview
                                        Comment on overall performance based on score.
                                        
                                        ## Nutritional Analysis
                                        Analyze calories, protein, carbs, fats and fiber.
                                        Mention strengths and deficiencies.
                                        
                                        ## Recommendations
                                        Suggest Indian foods to improve weak areas.
                                        
                                        ## Next Week Goals
                                        Give 3 practical action points.
                                        
                                        ## Summary
                                        End with motivating coach-style feedback.
                                        
                                        Data:
                                        Days tracked: %d
                                        Average Diet Score: %.1f/100
                                        Calories: %.0f kcal
                                        Protein: %.1fg
                                        Carbs: %.1fg
                                        Fats: %.1fg
                                        Fiber: %.1fg
                                        
                                        Rules:
                                        - 250-400 words
                                        - Specific, not generic
                                        - Professional tone
                                        - Use bullets where useful
                                        - Return markdown only
                                        -Put a blank line after every heading.
                                        """,
                days,
                avgScore,
                avg.getCalories(),
                avg.getProtein(),
                avg.getCarbs(),
                avg.getFats(),
                avg.getFiber()
        );

        try {
            return chatModel.call(prompt);
        } catch(Exception e) {
            return """
                    ## Weekly Overview
                    Your dietary consistency was fairly good this week with room for optimization.
                    
                    ## Nutritional Analysis
                    Protein and fiber need improvement while maintaining calorie balance.
                    
                    ## Recommendations
                    Add paneer, dal, sprouts, curd, oats and fruit to improve nutrition quality.
                    
                    ## Next Week Goals
                    1. Increase protein at each meal
                    2. Improve fiber intake daily
                    3. Keep diet score above 80
                    
                    ## Summary
                    Good progress this week. Focus on consistency and nutrient quality next week.
                    """;
        }
    }
}