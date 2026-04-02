package com.healthtracker.HealthTracker.Service;

import com.healthtracker.HealthTracker.DTO.CodeDTOs;
import com.healthtracker.HealthTracker.Entity.DietRecord;
import com.healthtracker.HealthTracker.Entity.MealEntry;
import com.healthtracker.HealthTracker.Entity.User;
import com.healthtracker.HealthTracker.Repository.DietRecordRepository;
import com.healthtracker.HealthTracker.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietAnalysisService {

    private final UserRepo userRepository;
    private final DietRecordRepository dietRecordRepository;

    private final MealProcessorService mealProcessor;
    private final NutritionAggregator aggregator;
    private final NutritionAnalysisService analysisService;
    private final DietScoreCalculator scoreCalculator;
    private final RecommendationEngine recommendationEngine;
    private final DietRecordMapper mapper;
    private final NutritionCalculatorService targetCalculator;

    @Transactional
    public CodeDTOs.DietRecordResponse createDietRecord(String email, CodeDTOs.CreateDietRecordRequest request) {

        User user = userRepository.findByemail(email).orElseThrow(() -> new RuntimeException("User not found"));

        DietRecord record = new DietRecord();
        record.setUser(user);
        record.setRecordDate(
                request.getRecordDate() != null ? request.getRecordDate() : LocalDate.now()
        );

        // 1. Process Meals
        List<MealEntry> meals = request.getMeals().stream()
                .map(req -> mealProcessor.process(req, record))
                .toList();

        // 2. Aggregate
        CodeDTOs.NutritionalBreakdown totals = aggregator.aggregate(meals);

        // 3. Targets
        CodeDTOs.NutritionalTargets targets = targetCalculator.caltulateTargets(user);

        // 4. Analysis
        List<CodeDTOs.DietAnalysis> analysis = analysisService.analyze(totals, targets);

        // 5. Score
        int score = scoreCalculator.calculate(analysis);

        // 6. Recommendations
        List<CodeDTOs.Recommendation> recs =
                recommendationEngine.generate(analysis, user.getGoal());

        // 7. Save
        record.setMeals(meals);
        record.setTotalCalories(totals.getCalories());
        record.setTotalProtein(totals.getProtein());
        record.setTotalCarbs(totals.getCarbs());
        record.setTotalFats(totals.getFats());
        record.setTotalFiber(totals.getFiber());
        record.setDietScore(score);

        dietRecordRepository.save(record);

        // 8. Response
        return mapper.toResponse(record, totals, analysis, recs);
    }
    public List<CodeDTOs.DietRecordResponse> getUserDietRecords(Long userId) {
        List<DietRecord> records = dietRecordRepository.findByUserIdOrderByRecordDateDesc(userId);

        return records.stream()
                .map(record -> {
                    CodeDTOs.NutritionalBreakdown totals = new CodeDTOs.NutritionalBreakdown(
                            record.getTotalCalories(),
                            record.getTotalProtein(),
                            record.getTotalCarbs(),
                            record.getTotalFats(),
                            record.getTotalFiber()
                    );

                    // Parse stored analysis and recommendations
                    List<CodeDTOs.DietAnalysis> analysis = deserializeAnalysis(record.getAnalysis());
                    List<CodeDTOs.Recommendation> recommendations = deserializeRecommendations(record.getRecommendations());

                    return mapper.toResponse(record, totals, analysis, recommendations);
                })
                .toList();
    }
    private List<CodeDTOs.DietAnalysis> deserializeAnalysis(String analysisStr) {
        // In production, use JSON deserialization
        return new ArrayList<>(); // Placeholder
    }
    private List<CodeDTOs.Recommendation> deserializeRecommendations(String recsStr) {
        // In production, use JSON deserialization
        return new ArrayList<>(); // Placeholder
    }
}
