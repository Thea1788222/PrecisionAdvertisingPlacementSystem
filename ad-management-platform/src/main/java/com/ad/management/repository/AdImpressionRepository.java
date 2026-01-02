package com.ad.management.repository;

import com.ad.management.model.entity.AdImpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdImpressionRepository extends JpaRepository<AdImpression, Long> {
    
    @Query("SELECT ai FROM AdImpression ai WHERE " +
           "(:adId IS NULL OR ai.adId = :adId) AND " +
           "(:startDate IS NULL OR ai.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ai.createdAt <= :endDate)")
    List<AdImpression> findByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(ai) FROM AdImpression ai WHERE " +
           "(:adId IS NULL OR ai.adId = :adId) AND " +
           "ai.isClicked = 1 AND " +
           "(:startDate IS NULL OR ai.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ai.createdAt <= :endDate)")
    Long countClicksByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COUNT(ai) FROM AdImpression ai WHERE " +
           "(:adId IS NULL OR ai.adId = :adId) AND " +
           "(:startDate IS NULL OR ai.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ai.createdAt <= :endDate)")
    Long countImpressionsByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}