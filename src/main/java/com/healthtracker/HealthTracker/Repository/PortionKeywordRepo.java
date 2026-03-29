package com.healthtracker.HealthTracker.Repository;

import com.healthtracker.HealthTracker.Entity.PortionKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PortionKeywordRepo extends JpaRepository<PortionKeyword , Long > {
    List<PortionKeyword> findByKeywordIn(Collection<String> keywords);
}
