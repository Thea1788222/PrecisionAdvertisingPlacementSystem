package com.ad.management.repository;

import com.ad.management.model.TrafficStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrafficStatisticRepository extends JpaRepository<TrafficStatisticEntity, Long> {
    
    /**
     * 根据网站名称和日期范围查询流量统计数据
     *
     * @param website   网站名称（可选）
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 符合条件的流量统计数据列表
     */
    @Query("SELECT t FROM TrafficStatisticEntity t WHERE " +
           "(:website IS NULL OR t.website = :website) AND " +
           "(:startDate IS NULL OR t.date >= :startDate) AND " +
           "(:endDate IS NULL OR t.date <= :endDate)")
    List<TrafficStatisticEntity> findByWebsiteAndDateRange(
        @Param("website") String website,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}