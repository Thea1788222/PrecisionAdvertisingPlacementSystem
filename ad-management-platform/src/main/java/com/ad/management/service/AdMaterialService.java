package com.ad.management.service;

import com.ad.management.model.AdMaterial;
import com.ad.management.repository.AdMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdMaterialService {
    
    private final AdMaterialRepository adMaterialRepository;
    
    public AdMaterialService(AdMaterialRepository adMaterialRepository) {
        this.adMaterialRepository = adMaterialRepository;
    }
    
    public Page<AdMaterial> getAllAdMaterials(Long advertiserId, String type, String category, Integer status, Pageable pageable) {
        return adMaterialRepository.findByFilters(advertiserId, type, category, status, pageable);
    }
    
    public AdMaterial createAdMaterial(AdMaterial adMaterial) {
        return adMaterialRepository.save(adMaterial);
    }
    
    public Optional<AdMaterial> getAdMaterialById(Long id) {
        return adMaterialRepository.findById(id);
    }
    
    public AdMaterial updateAdMaterial(Long id, AdMaterial adMaterialDetails) {
        AdMaterial adMaterial = adMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告素材未找到"));
        
        adMaterial.setTitle(adMaterialDetails.getTitle());
        adMaterial.setImageUrl(adMaterialDetails.getImageUrl());
        adMaterial.setVideoUrl(adMaterialDetails.getVideoUrl());
        adMaterial.setLinkUrl(adMaterialDetails.getLinkUrl());
        adMaterial.setWidth(adMaterialDetails.getWidth());
        adMaterial.setHeight(adMaterialDetails.getHeight());
        adMaterial.setDuration(adMaterialDetails.getDuration());
        adMaterial.setCategory(adMaterialDetails.getCategory());
        adMaterial.setTargetInterest(adMaterialDetails.getTargetInterest());
        adMaterial.setBidPrice(adMaterialDetails.getBidPrice());
        adMaterial.setStatus(adMaterialDetails.getStatus());
        
        return adMaterialRepository.save(adMaterial);
    }
    
    public void deleteAdMaterial(Long id) {
        AdMaterial adMaterial = adMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告素材未找到"));
        adMaterialRepository.delete(adMaterial);
    }
}