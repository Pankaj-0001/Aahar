package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.DietRecord;
import com.aahar.Aahar.Entity.MealEntry;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Repository.DietRecordRepository;
import com.aahar.Aahar.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ObjectMapper objectMapper;

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
        record.setAnalysis(objectMapper.writeValueAsString(analysis));
        record.setRecommendations(objectMapper.writeValueAsString(recs));
        record.setCreatedAt(LocalDateTime.now());
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
        if (analysisStr == null || analysisStr.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(analysisStr, new TypeReference<List<CodeDTOs.DietAnalysis>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<CodeDTOs.Recommendation> deserializeRecommendations(String recsStr) {
        if (recsStr == null || recsStr.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(recsStr, new TypeReference<List<CodeDTOs.Recommendation>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
