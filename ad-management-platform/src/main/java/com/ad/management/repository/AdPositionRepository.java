package com.ad.management.repository;

import com.ad.management.model.AdPosition;
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
}