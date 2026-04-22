package com.aahar.Aahar.Repository;

import com.aahar.Aahar.Entity.DietRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietRecordRepository extends JpaRepository<DietRecord,Long> {
    List<DietRecord> findByUserIdOrderByRecordDateDesc(Long userId);
    Optional<DietRecord> findByUserIdAndRecordDate(Long userId, LocalDate recordDate);
    List<DietRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
            Long userId, LocalDate start, LocalDate end
    );
}
