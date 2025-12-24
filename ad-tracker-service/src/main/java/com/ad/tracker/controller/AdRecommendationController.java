package com.ad.tracker.controller;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.service.AdRecommendationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ad")

public class AdRecommendationController {
    
    @Autowired
    private AdRecommendationService adRecommendationService;
    
    /**
     * 获取推荐广告
     */
    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getRecommendedAds(@RequestBody Map<String, Object> request) {

        String userFingerprint = (String) request.get("userFingerprint");
        String website = (String) request.get("website");
        @SuppressWarnings("unchecked")
        List<String> positions = (List<String>) request.get("positions");
        String category = (String) request.get("category");
        int count = ((Number) request.get("count")).intValue();
        
        List<AdMaterial> ads = adRecommendationService.getRecommendedAds(userFingerprint, website, positions, category, count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ads", ads);
        return ResponseEntity.ok(response);
    }
}