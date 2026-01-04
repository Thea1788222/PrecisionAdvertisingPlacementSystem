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

    /**
     * 根据广告id和日期范围查询广告展示数据
     * @param adId 广告id
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 广告展示数据列表
     */
    @Query("SELECT ai FROM AdImpression ai WHERE " +
           "(:adId IS NULL OR ai.adId = :adId) AND " +
           "(:startDate IS NULL OR ai.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR ai.createdAt <= :endDate)")
    List<AdImpression> findByAdIdAndDateRange(
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

    /**
     * 根据广告id和日期范围统计广告展示次数
     * @param adId 广告id
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 广告展示次数
     */
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