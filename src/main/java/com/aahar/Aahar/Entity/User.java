package com.aahar.Aahar.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password="";

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double height;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private DietGoal goal;

    private String googleId;

    private boolean profileComplete = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietRecord> dietRecords = new ArrayList<>();


    public enum Gender {
        MALE, FEMALE, OTHER
    }
    public enum ActivityLevel {
        SEDENTARY,
        LIGHT,
        MODERATE,
        ACTIVE,
        VERY_ACTIVE
    }
    public enum DietGoal {
        MAINTENANCE,
        WEIGHT_LOSS,
        WEIGHT_GAIN,
        MUSCLE_BUILDING
    }
}
