package com.ad.tracker.controller;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.service.AdRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ad")
@Tag(name = "广告推荐", description = "广告推荐相关接口")
public class AdRecommendationController {
    
    @Autowired
    private AdRecommendationService adRecommendationService;
    
    /**
     * 获取推荐广告
     */
    @PostMapping("/recommend")
    @Operation(summary = "获取推荐广告", description = "根据用户画像和上下文信息获取推荐广告")
    @ApiResponse(responseCode = "200", description = "广告推荐成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> getRecommendedAds(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "推荐请求参数") 
        @RequestBody Map<String, Object> request) {

        String userFingerprint = (String) request.get("userFingerprint");
        String website = (String) request.get("website");
        @SuppressWarnings("unchecked")
        List<String> positions = (List<String>) request.get("positions");
        String category = (String) request.get("category");
        int count = ((Number) request.get("count")).intValue();
        
        List<AdMaterial> ads = adRecommendationService.getRecommendedAds(
            userFingerprint, website, positions, category, count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ads", ads);
        return ResponseEntity.ok(response);
    }
}