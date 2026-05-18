package com.aahar.Aahar.Repository;

import com.aahar.Aahar.Entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    Optional<WeeklyReport> findByUserIdAndWeekStartAndWeekEnd(
            Long userId, LocalDate weekStart, LocalDate weekEnd
    );
}