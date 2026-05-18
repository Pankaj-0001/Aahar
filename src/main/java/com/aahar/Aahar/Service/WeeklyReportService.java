package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.DietRecord;
import com.aahar.Aahar.Repository.DietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        long totalDays =
                ChronoUnit.DAYS.between(weekStart, weekEnd) + 1;

        // Minimum 7 days validation
        if (totalDays < 7) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum 7 days required for report generation"
            );
        }

        // Multiple of 7 validation
        if (totalDays % 7 != 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date range must be in multiples of 7"
            );
        }

        List<DietRecord> records =
                dietRecordRepository
                        .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                                userId,
                                weekStart,
                                weekEnd
                        );

        // Continuous data validation
        LocalDate current = weekStart;

        while (!current.isAfter(weekEnd)) {

            LocalDate finalCurrent = current;

            boolean exists = records.stream()
                    .anyMatch(record ->
                            record.getRecordDate().equals(finalCurrent)
                    );

            if (!exists) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing nutrition data for " + current
                );
            }

            current = current.plusDays(1);
        }

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

        String insights = generateInsights(
                averageScore,
                averageNutrition,
                records
        );

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
            CodeDTOs.NutritionalBreakdown nutrition,
            List<DietRecord> records
    ) {

        String dailyData =
                records.stream()
                        .map(r -> """
                                Date: %s
                                Score: %d
                                Calories: %.0f
                                Protein: %.1f
                                Carbs: %.1f
                                Fats: %.1f
                                Fiber: %.1f
                                """.formatted(
                                r.getRecordDate(),
                                r.getDietScore(),
                                r.getTotalCalories(),
                                r.getTotalProtein(),
                                r.getTotalCarbs(),
                                r.getTotalFats(),
                                r.getTotalFiber()
                        ))
                        .collect(Collectors.joining("\n"));

        String prompt = """
                You are an Indian nutrition expert analyzing a user's diet history.

                Write a realistic and practical nutrition report.

                Do NOT:
                - act like a therapist
                - praise the user
                - use motivational language
                - use corporate wellness tone
                - introduce yourself
                - exaggerate health claims

                Focus on:
                - diet consistency
                - calorie patterns
                - protein intake
                - carbs and fats balance
                - fiber intake
                - possible health effects
                - practical Indian food improvements
                - trends across days

                Write naturally like a real nutritionist.

                Keep it around 200-300 words.

                No markdown.
                No bullet points.

                Average Data:

                Score: %s
                Calories: %.0f
                Protein: %.1f
                Carbs: %.1f
                Fats: %.1f
                Fiber: %.1f

                Daily Records:

                %s
                """.formatted(
                score,
                nutrition.getCalories(),
                nutrition.getProtein(),
                nutrition.getCarbs(),
                nutrition.getFats(),
                nutrition.getFiber(),
                dailyData
        );

        try {

            return chatModel.call(prompt);

        } catch (Exception e) {

            return """
                    Diet report could not be generated.

                    Protein intake appears lower than ideal in several entries.
                    Fiber intake can improve through fruits, vegetables, and salads.

                    Try maintaining more consistency in meal timing and nutrition balance.
                    """;
        }
    }
}