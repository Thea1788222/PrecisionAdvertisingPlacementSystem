package com.ad.tracker.controller;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.AdImpression;
import com.ad.tracker.service.UserBehaviorService;
import com.ad.tracker.service.AdImpressionService;
import com.ad.tracker.service.AdRecommendationService;
import com.ad.tracker.model.AdMaterial;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/track")
@Tag(name = "行为追踪", description = "用户行为追踪、广告展示与点击记录相关接口")
public class TrackController {
    
    private static final Logger logger = Logger.getLogger(TrackController.class.getName());
    
    @Autowired
    private UserBehaviorService userBehaviorService;
    
    @Autowired
    private AdImpressionService adImpressionService;
    
    @Autowired
    private AdRecommendationService adRecommendationService;
    
    /**
     * 记录用户行为
     */
    @PostMapping("/behavior")
    @Operation(summary = "记录用户行为", description = "记录用户在网站上的各种行为，如页面浏览、搜索、点击等")
    @ApiResponse(responseCode = "200", description = "行为记录成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> trackBehavior(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户行为信息") 
        @RequestBody UserBehavior userBehavior) {
        try {
            userBehaviorService.saveUserBehavior(userBehavior);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "行为记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "记录用户行为时出错", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "记录用户行为失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 记录广告展示
     */
    @PostMapping("/impression")
    @Operation(summary = "记录广告展示", description = "记录广告在用户界面上的展示事件")
    @ApiResponse(responseCode = "200", description = "广告展示记录成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> trackImpression(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "广告展示信息") 
        @RequestBody Map<String, Object> request) {
        try {
            Long adId = ((Number) request.get("adId")).longValue();
            String userFingerprint = (String) request.get("userFingerprint");
            String website = (String) request.get("website");
            String position = (String) request.get("position");
            BigDecimal bidPrice = new BigDecimal(request.get("bidPrice").toString());
            
            AdImpression impression = adImpressionService.saveAdImpression(
                adId, userFingerprint, website, position, bidPrice);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("impressionId", impression.getId());
            response.put("message", "展示记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "记录广告展示时出错", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "记录广告展示失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 记录广告点击
     */
    @PostMapping("/click")
    @Operation(summary = "记录广告点击", description = "记录用户对广告的点击行为")
    @ApiResponse(responseCode = "200", description = "广告点击记录成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> trackClick(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "广告点击信息") 
        @RequestBody Map<String, Object> request) {
        try {
            Long impressionId = ((Number) request.get("impressionId")).longValue();
            String userFingerprint = (String) request.get("userFingerprint");
            
            adImpressionService.updateAdClick(impressionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "点击记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "记录广告点击时出错", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "记录广告点击失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}