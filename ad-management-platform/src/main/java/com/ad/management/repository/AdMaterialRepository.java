package com.ad.management.repository;

import com.ad.management.model.AdMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdMaterialRepository extends JpaRepository<AdMaterial, Long> {

    /**
     * 根据广告主ID、广告类型、广告分类、状态、关键词分页查询广告素材
     *
     * @param advertiserId 广告主ID（可选）
     * @param type         广告类型（可选）
     * @param category     广告分类（可选）
     * @param status       状态（可选）
     * @param keyword      关键词（可选）
     * @param pageable     分页信息
     * @return 符合条件的广告素材分页列表
     */
    @Query("SELECT am FROM AdMaterial am WHERE " +
           "(:advertiserId IS NULL OR am.advertiserId = :advertiserId) AND " +
           "(:type IS NULL OR am.type = :type) AND " +
           "(:category IS NULL OR am.category = :category) AND " +
           "(:status IS NULL OR am.status = :status) AND " +
           "(:keyword IS NULL OR am.title LIKE %:keyword%)")
    Page<AdMaterial> findByFilters(
        @Param("advertiserId") Long advertiserId,
        @Param("type") String type,
        @Param("category") String category,
        @Param("status") Integer status,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}