package com.healthtracker.HealthTracker.Repository;

import com.healthtracker.HealthTracker.Entity.FoodItem;
import com.healthtracker.HealthTracker.Entity.PortionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortionMappingRepo extends JpaRepository<PortionMapping, Long> {

    Optional<PortionMapping> findByFoodItemAndPortionTypeAndPortionSize(
            FoodItem foodItem,
            PortionMapping.PortionType portionType,
            PortionMapping.PortionSize portionSize
    );
}
