package com.ad.management.repository;

import com.ad.management.model.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    
    @Query("SELECT ub FROM UserBehavior ub WHERE " +
           "ub.targetId LIKE 'ad_%' AND " +
           "ub.actionType = 'click' AND " +
           "(:adId IS NULL OR ub.targetId = CONCAT('ad_', :adId)) AND " +
           "(:startDate IS NULL OR ub.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ub.createdAt <= :endDate)")
    List<UserBehavior> findAdClickBehaviorsByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE " +
           "ub.targetId LIKE 'ad_%' AND " +
           "ub.actionType = 'click' AND " +
           "(:adId IS NULL OR ub.targetId = CONCAT('ad_', :adId)) AND " +
           "(:startDate IS NULL OR ub.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ub.createdAt <= :endDate)")
    Long countAdClicksByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}