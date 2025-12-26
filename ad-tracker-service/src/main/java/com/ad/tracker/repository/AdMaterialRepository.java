package com.ad.tracker.repository;

import com.ad.tracker.model.AdMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdMaterialRepository extends JpaRepository<AdMaterial, Long> {
    List<AdMaterial> findByCategoryAndStatusOrderByBidPriceDesc(String category, Integer status);

    List<AdMaterial> findByStatus(Integer status);
    
    /**
     * 根据类别、类型和状态查询广告素材，按出价降序排列
     */
    @Query("SELECT am FROM AdMaterial am WHERE am.category = :category AND am.type = :type AND am.status = :status ORDER BY am.bidPrice DESC")
    List<AdMaterial> findByCategoryAndTypeAndStatusOrderByBidPriceDesc(@Param("category") String category, @Param("type") String type, @Param("status") Integer status);
    
    /**
     * 根据类别和状态查询广告素材（排除指定类型），按出价降序排列
     */
    @Query("SELECT am FROM AdMaterial am WHERE am.category = :category AND am.status = :status AND am.type != :excludedType ORDER BY am.bidPrice DESC")
    List<AdMaterial> findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(@Param("category") String category, @Param("excludedType") String excludedType, @Param("status") Integer status);
    
    /**
     * 根据状态查询广告素材（排除指定类型）
     */
    @Query("SELECT am FROM AdMaterial am WHERE am.status = :status AND am.type != :excludedType")
    List<AdMaterial> findByStatusExcludingType(@Param("status") Integer status, @Param("excludedType") String excludedType);
}