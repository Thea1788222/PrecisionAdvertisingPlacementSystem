package com.ad.management.controller;

import com.ad.management.model.AdPosition;
import com.ad.management.service.AdPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
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
     * @param positionKey 位置标识（可选）
     * @param positionName 位置名称（可选）
     * @param page 页码（从0开始，默认为0）
     * @param size 每页大小（默认为10）
     * @return 广告位配置列表
     */
    @GetMapping
    public ResponseEntity<Page<AdPosition>> getAllAdPositions(
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String positionKey,
            @RequestParam(required = false) String positionName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdPosition> positions;
        
        // 如果有任何搜索条件，则使用条件搜索方法
        if (website != null || positionKey != null || positionName != null) {
            positions = adPositionService.getAdPositionsByConditions(website, positionKey, positionName, pageable);
        } else {
            // 否则获取所有广告位
            positions = adPositionService.getAllAdPositions(pageable);
        }
        
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
     * 根据ID更新广告位配置
     *
     * @param id 广告位配置ID
     * @param adPositionDetails 广告位配置详情
     * @return 更新后的广告位配置
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