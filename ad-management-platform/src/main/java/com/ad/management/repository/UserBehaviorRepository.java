package com.ad.management.repository;

import com.ad.management.model.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    /**
     * 根据广告id和日期范围查询广告点击行为
     * @param adId 广告id
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 广告点击行为列表
     */
    @Query("SELECT ub FROM UserBehavior ub WHERE " +
           "ub.targetId LIKE 'ad_%' AND " +
           "ub.actionType = 'click' AND " +
           "(:adId IS NULL OR ub.targetId = CONCAT('ad_', :adId)) AND " +
           "(:startDate IS NULL OR ub.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ub.createdAt <= :endDate)")
    List<UserBehavior> findAdClickBehaviorsByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 根据广告id和日期范围统计广告点击次数
     * @param adId 广告id
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 广告点击次数
     */
    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE " +
           "ub.targetId LIKE 'ad_%' AND " +
           "ub.actionType = 'click' AND " +
           "(:adId IS NULL OR ub.targetId = CONCAT('ad_', :adId)) AND " +
           "(:startDate IS NULL OR ub.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ub.createdAt <= :endDate)")
    Long countAdClicksByAdIdAndDateRange(
        @Param("adId") Long adId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}