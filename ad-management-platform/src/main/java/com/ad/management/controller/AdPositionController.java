package com.ad.management.controller;

import com.ad.management.model.AdPosition;
import com.ad.management.service.AdPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/positions")
public class AdPositionController {

    @Autowired
    private AdPositionService adPositionService;

    /**
     * 获取所有广告位配置
     *
     * @param website 网站域名（可选）
     * @return 广告位配置列表
     */
    @GetMapping
    public ResponseEntity<List<AdPosition>> getAllAdPositions(
            @RequestParam(required = false) String website) {
        List<AdPosition> positions = adPositionService.getAllAdPositions(website);
        return ResponseEntity.ok(positions);
    }

    /**
     * 创建新的广告位配置
     *
     * @param adPosition 新的广告位配置
     * @return 创建成功的广告位配置
     */
    @PostMapping
    public ResponseEntity<AdPosition> createAdPosition(@RequestBody AdPosition adPosition) {
        AdPosition createdPosition = adPositionService.createAdPosition(adPosition);
        return ResponseEntity.ok(createdPosition);
    }

    /**
     * 根据ID获取广告位配置
     *
     * @param id 广告位配置ID
     * @return 匹配的广告位配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdPosition> updateAdPosition(
            @PathVariable Long id, 
            @RequestBody AdPosition adPositionDetails) {
        AdPosition updatedPosition = adPositionService.updateAdPosition(id, adPositionDetails);
        return ResponseEntity.ok(updatedPosition);
    }

     /**
      * 删除广告位配置
      *
      * @param id 广告位配置ID
      * @return 删除成功消息
      */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdPosition(@PathVariable Long id) {
        adPositionService.deleteAdPosition(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "广告位配置删除成功");
        return ResponseEntity.ok(response);
    }
}