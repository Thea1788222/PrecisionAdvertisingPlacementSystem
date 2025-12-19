package com.ad.management.repository;

import com.ad.management.model.AdPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdPositionRepository extends JpaRepository<AdPosition, Long> {
    
    /**
     * 根据网站名称查询广告位置
     *
     * @param website 网站名称（可选）
     * @return 符合条件的广告位置列表
     */
    @Query("SELECT ap FROM AdPosition ap WHERE (:website IS NULL OR ap.website = :website)")
    List<AdPosition> findByWebsite(@Param("website") String website);
    
    /**
     * 根据多个条件查询广告位置（分页）
     *
     * @param website 网站名称（可选）
     * @param positionKey 位置标识（可选）
     * @param positionName 位置名称（可选）
     * @param pageable 分页参数
     * @return 符合条件的广告位置分页列表
     */
    @Query("SELECT ap FROM AdPosition ap WHERE " +
           "(:website IS NULL OR ap.website = :website) AND " +
           "(:positionKey IS NULL OR ap.positionKey LIKE CONCAT('%', :positionKey, '%')) AND " +
           "(:positionName IS NULL OR ap.positionName LIKE CONCAT('%', :positionName, '%'))")
    Page<AdPosition> findByConditions(
        @Param("website") String website,
        @Param("positionKey") String positionKey,
        @Param("positionName") String positionName,
        Pageable pageable
    );
}