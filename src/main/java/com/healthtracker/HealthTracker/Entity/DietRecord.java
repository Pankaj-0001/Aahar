package com.healthtracker.HealthTracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="diet_record")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DietRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private LocalDate recordDate;

    private Double totalCalories;
    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFats;
    private Double totalFiber;

    private Integer dietScore;

    @Column(length = 2000)
    private String analysis;

    @Column(length = 3000)
    private String recommendations;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "dietRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealEntry> meals = new ArrayList<>();

}
