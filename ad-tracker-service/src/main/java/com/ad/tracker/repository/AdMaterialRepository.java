package com.ad.tracker.repository;

import com.ad.tracker.model.AdMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdMaterialRepository extends JpaRepository<AdMaterial, Long> {
    List<AdMaterial> findByCategoryAndStatusOrderByBidPriceDesc(String category, Integer status);
}