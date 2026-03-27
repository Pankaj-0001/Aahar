package com.healthtracker.HealthTracker.Repository;

import com.healthtracker.HealthTracker.Entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<FoodItem,Long> {

    List<FoodItem> findByNameContainingIgnoreCase(String query);
    List<FoodItem> findByCategory(String category);
    Optional<FoodItem> findByid(Long id);
}
