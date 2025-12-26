package com.ad.tracker.controller;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.AdImpression;
import com.ad.tracker.service.AdImpressionService;
import com.ad.tracker.service.UserBehaviorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/track")

public class TrackController {
    
    private static final Logger logger = Logger.getLogger(TrackController.class.getName());
    
    @Autowired
    private UserBehaviorService userBehaviorService;
    
    @Autowired
    private AdImpressionService adImpressionService;
    
    /**
     * 记录用户行为
     */
    @PostMapping("/behavior")
    public ResponseEntity<Map<String, Object>> trackBehavior(@RequestBody UserBehavior userBehavior) {
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
    public ResponseEntity<Map<String, Object>> trackImpression(@RequestBody Map<String, Object> request) {

        try {
            Long adId = ((Number) request.get("adId")).longValue();
            String userFingerprint = (String) request.get("userFingerprint");
            String website = (String) request.get("website");
            String position = (String) request.get("position");
            BigDecimal bidPrice = new BigDecimal(request.get("bidPrice").toString());
            
            AdImpression impression = adImpressionService.saveAdImpression(adId, userFingerprint, website, position, bidPrice);
            
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
    public ResponseEntity<Map<String, Object>> trackClick(@RequestBody Map<String, Object> request) {

        try {
            Long impressionId = ((Number) request.get("impressionId")).longValue();

            // 更新广告点击记录
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