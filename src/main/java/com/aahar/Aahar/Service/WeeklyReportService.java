package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.DietRecord;
import com.aahar.Aahar.Repository.DietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final DietRecordRepository dietRecordRepository;
    private final ChatModel chatModel;

    public CodeDTOs.WeeklyProgressResponse getWeeklyReport(
            Long userId,
            LocalDate weekStart,
            LocalDate weekEnd
    ) {

        List<DietRecord> records =
                dietRecordRepository
                        .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                                userId,
                                weekStart,
                                weekEnd
                        );

        List<CodeDTOs.DailyProgress> dailyProgress =
                records.stream()
                        .map(record -> new CodeDTOs.DailyProgress(
                                record.getRecordDate(),
                                record.getDietScore(),

                                new CodeDTOs.NutritionalBreakdown(
                                        record.getTotalCalories(),
                                        record.getTotalProtein(),
                                        record.getTotalCarbs(),
                                        record.getTotalFats(),
                                        record.getTotalFiber()
                                )
                        ))
                        .toList();

        double averageScore =
                records.stream()
                        .mapToInt(DietRecord::getDietScore)
                        .average()
                        .orElse(0);

        CodeDTOs.NutritionalBreakdown averageNutrition =
                new CodeDTOs.NutritionalBreakdown(

                        average(records, DietRecord::getTotalCalories),
                        average(records, DietRecord::getTotalProtein),
                        average(records, DietRecord::getTotalCarbs),
                        average(records, DietRecord::getTotalFats),
                        average(records, DietRecord::getTotalFiber)
                );

        String insights;

        if (records.isEmpty()) {

            insights = "No weekly data available.";

        } else {

            insights = generateInsights(
                    averageScore,
                    averageNutrition
            );
        }

        return new CodeDTOs.WeeklyProgressResponse(
                weekStart,
                weekEnd,
                averageScore,
                averageNutrition,
                dailyProgress,
                insights
        );
    }

    private double average(
            List<DietRecord> records,
            Function<DietRecord, Double> getter
    ) {

        return records.stream()
                .mapToDouble(record -> {

                    Double value = getter.apply(record);

                    return value == null ? 0 : value;
                })
                .average()
                .orElse(0);
    }

    private String generateInsights(
            double score,
            CodeDTOs.NutritionalBreakdown nutrition
    ) {

        String prompt =  """
            You are an Indian nutrition expert analyzing a user's weekly diet.
            
            Write a realistic and natural diet report.
            
            Do NOT:
            - act like a therapist
            - praise the user
            - say "good job"
            - say "first step"
            - introduce yourself
            - use corporate wellness language
            - use motivational filler
            
            Write like a real nutritionist giving practical analysis.
            
            The report should cover:
            - overall diet quality
            - calorie intake
            - protein intake
            - carbs and fats
            - fiber intake
            - possible health effects
            - practical Indian food improvements
            
            Keep the tone direct, human, and informative.
            
            Write in normal paragraphs.
            No markdown.
            Keep it around 200-300 words.
            
            Data:
            
            Score: %s
            Calories: %.0f
            Protein: %.1f
            Carbs: %.1f
            Fats: %.1f
            Fiber: %.1f
            """.formatted(
                score,
                nutrition.getCalories(),
                nutrition.getProtein(),
                nutrition.getCarbs(),
                nutrition.getFats(),
                nutrition.getFiber()
        );

        try {

            return chatModel.call(prompt);

        } catch (Exception e) {

            return """
                    Weekly diet tracking completed.

                    Protein intake can improve.
                    Add more fruits, salads, paneer, and dal.

                    Maintain consistency next week.
                    """;
        }
    }
}