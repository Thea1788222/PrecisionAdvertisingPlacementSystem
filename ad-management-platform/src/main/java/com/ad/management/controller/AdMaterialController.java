package com.ad.management.controller;

import com.ad.management.model.AdMaterial;
import com.ad.management.model.FileUploadResponse;
import com.ad.management.service.AdMaterialService;
import com.ad.management.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class AdMaterialController {

    @Autowired
    private AdMaterialService adMaterialService;
    
    @Autowired
    private OssService ossService;

    /**
     * 获取所有广告素材
     *
     * @param page 页码
     * @param size 每页数量
     * @param advertiserId 广告主ID
     * @param type 素材类型
     * @param category 素材分类
     * @param status 素材状态
     * @param keyword 搜索关键词
     * @return 广告素材分页列表
     */
    @GetMapping
    public ResponseEntity<Page<AdMaterial>> getAllAdMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdMaterial> materials = adMaterialService.getAllAdMaterials(advertiserId, type, category, status, keyword, pageable);
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
    
    /**
     * 上传广告素材文件到阿里云 OSS
     *
     * @param file 文件
     * @return 文件上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadMaterialFile(
            @RequestParam("file") MultipartFile file) {
        try {
            // 上传文件到阿里云 OSS
            String fileUrl = ossService.uploadFile(file, "ad-materials");
            
            // 返回成功响应
            FileUploadResponse response = new FileUploadResponse(true, fileUrl, "文件上传成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 返回失败响应
            FileUploadResponse response = new FileUploadResponse(false, null, "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}