package com.ad.management.controller;

import com.ad.management.model.AdMaterial;
import com.ad.management.service.AdMaterialService;
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
    
    private final AdMaterialService adMaterialService;
    
    public AdMaterialController(AdMaterialService adMaterialService) {
        this.adMaterialService = adMaterialService;
    }
    
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
    
    @PostMapping
    public ResponseEntity<AdMaterial> createAdMaterial(@RequestBody AdMaterial adMaterial) {
        AdMaterial createdMaterial = adMaterialService.createAdMaterial(adMaterial);
        return ResponseEntity.ok(createdMaterial);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AdMaterial> updateAdMaterial(
            @PathVariable Long id, 
            @RequestBody AdMaterial adMaterialDetails) {
        AdMaterial updatedMaterial = adMaterialService.updateAdMaterial(id, adMaterialDetails);
        return ResponseEntity.ok(updatedMaterial);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdMaterial(@PathVariable Long id) {
        adMaterialService.deleteAdMaterial(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "广告素材删除成功");
        return ResponseEntity.ok(response);
    }
}