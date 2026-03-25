package com.healthtracker.HealthTracker.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="portion_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PortionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortionType portionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortionSize portionSize;

    @Column(nullable = false)
    private Double gramsEquivalent;

    @Column(length = 500)
    private String description;

    public enum PortionType {
        ROTI, KATORI, GLASS, PIECE, BOWL, SPOON, HANDFUL
    }

    public enum PortionSize {
        SMALL, MEDIUM, LARGE, PATLI, MOTI, EXTRA_LARGE
    }


}
