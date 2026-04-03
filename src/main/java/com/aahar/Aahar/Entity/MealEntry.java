package com.aahar.Aahar.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="meal_entries")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    private String foodName;

    private String portionDescription;
    private Double quantity;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fats;
    private Double fiber;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_record_id", nullable = false)
    private DietRecord dietRecord;

    public enum MealType {
        PRE_BREAKFAST,
        BREAKFAST,
        LUNCH,
        EVENING_SNACK,
        DINNER,
        SNACK
    }


}
