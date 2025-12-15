package com.ad.management.controller;

import com.ad.management.model.AdMaterial;
import com.ad.management.service.AdMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class AdMaterialController {

    @Autowired
    private AdMaterialService adMaterialService;

    /**
     * 获取所有广告素材
     *
     * @param page 页码
     * @param size 每页数量
     * @param advertiserId 广告主ID
     * @param type 素材类型
     * @param category 素材分类
     * @param status 素材状态
     * @return 广告素材分页列表
     */
    @GetMapping
    public ResponseEntity<Page<AdMaterial>> getAllAdMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdMaterial> materials = adMaterialService.getAllAdMaterials(advertiserId, type, category, status, pageable);
        return ResponseEntity.ok(materials);
    }

    /**
     * 创建广告素材
     *
     * @param adMaterial 广告素材对象
     * @return 创建的广告素材对象
     */
    @PostMapping
    public ResponseEntity<AdMaterial> createAdMaterial(@RequestBody AdMaterial adMaterial) {
        AdMaterial createdMaterial = adMaterialService.createAdMaterial(adMaterial);
        return ResponseEntity.ok(createdMaterial);
    }

    /**
     * 获取广告素材详情
     *
     * @param id 广告素材ID
     * @return 广告素材对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdMaterial> updateAdMaterial(
            @PathVariable Long id, 
            @RequestBody AdMaterial adMaterialDetails) {
        AdMaterial updatedMaterial = adMaterialService.updateAdMaterial(id, adMaterialDetails);
        return ResponseEntity.ok(updatedMaterial);
    }

     /**
      * 删除广告素材
      *
      * @param id 广告素材ID
      * @return 删除成功消息
      */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdMaterial(@PathVariable Long id) {
        adMaterialService.deleteAdMaterial(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "广告素材删除成功");
        return ResponseEntity.ok(response);
    }
}