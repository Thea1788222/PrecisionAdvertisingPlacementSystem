package com.ad.management.service;

import com.ad.management.model.AdPosition;
import com.ad.management.repository.AdPositionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdPositionService {
    
    private final AdPositionRepository adPositionRepository;
    
    public AdPositionService(AdPositionRepository adPositionRepository) {
        this.adPositionRepository = adPositionRepository;
    }
    
    public List<AdPosition> getAllAdPositions(String website) {
        return adPositionRepository.findByWebsite(website);
    }
    
    public AdPosition createAdPosition(AdPosition adPosition) {
        return adPositionRepository.save(adPosition);
    }
    
    public Optional<AdPosition> getAdPositionById(Long id) {
        return adPositionRepository.findById(id);
    }
    
    public AdPosition updateAdPosition(Long id, AdPosition adPositionDetails) {
        AdPosition adPosition = adPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告位未找到"));
        
        adPosition.setPositionName(adPositionDetails.getPositionName());
        adPosition.setWidth(adPositionDetails.getWidth());
        adPosition.setHeight(adPositionDetails.getHeight());
        adPosition.setDescription(adPositionDetails.getDescription());
        
        return adPositionRepository.save(adPosition);
    }
    
    public void deleteAdPosition(Long id) {
        AdPosition adPosition = adPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告位未找到"));
        adPositionRepository.delete(adPosition);
    }
}