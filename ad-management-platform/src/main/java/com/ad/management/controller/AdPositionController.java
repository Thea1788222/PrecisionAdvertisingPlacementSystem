package com.ad.management.controller;

import com.ad.management.model.AdPosition;
import com.ad.management.service.AdPositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/positions")
public class AdPositionController {
    
    private final AdPositionService adPositionService;
    
    public AdPositionController(AdPositionService adPositionService) {
        this.adPositionService = adPositionService;
    }
    
    @GetMapping
    public ResponseEntity<List<AdPosition>> getAllAdPositions(
            @RequestParam(required = false) String website) {
        List<AdPosition> positions = adPositionService.getAllAdPositions(website);
        return ResponseEntity.ok(positions);
    }
    
    @PostMapping
    public ResponseEntity<AdPosition> createAdPosition(@RequestBody AdPosition adPosition) {
        AdPosition createdPosition = adPositionService.createAdPosition(adPosition);
        return ResponseEntity.ok(createdPosition);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AdPosition> updateAdPosition(
            @PathVariable Long id, 
            @RequestBody AdPosition adPositionDetails) {
        AdPosition updatedPosition = adPositionService.updateAdPosition(id, adPositionDetails);
        return ResponseEntity.ok(updatedPosition);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdPosition(@PathVariable Long id) {
        adPositionService.deleteAdPosition(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "广告位配置删除成功");
        return ResponseEntity.ok(response);
    }
}