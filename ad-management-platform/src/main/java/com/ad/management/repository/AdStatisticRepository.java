package com.ad.management.repository;

import com.ad.management.model.AdStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdStatisticRepository extends JpaRepository<AdStatistic, Long> {
    
    @Query("SELECT s FROM AdStatistic s WHERE " +
           "(:adId IS NULL OR s.adId = :adId) AND " +
           "(:startDate IS NULL OR s.date >= :startDate) AND " +
           "(:endDate IS NULL OR s.date <= :endDate)")
    List<AdStatistic> findByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}