package com.ad.management.controller;

import com.ad.management.model.Advertiser;
import com.ad.management.service.AdvertiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    @Autowired
    private AdvertiserService advertiserService;

    /**
     * 获取所有广告主配置
     *
     * @param page 页码（默认0）
     * @param size 每页数量（默认10）
     * @param name 广告主名称（可选）
     * @return 广告主配置分页列表
     */
    @GetMapping
    public ResponseEntity<Page<Advertiser>> getAllAdvertisers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Advertiser> advertisers = advertiserService.getAllAdvertisers(name, pageable);
        return ResponseEntity.ok(advertisers);
    }

    /**
     * 创建广告主配置
     *
     * @param advertiser 广告主配置对象
     * @return 创建成功的广告主配置对象
     */
    @PostMapping
    public ResponseEntity<Advertiser> createAdvertiser(@RequestBody Advertiser advertiser) {
        Advertiser createdAdvertiser = advertiserService.createAdvertiser(advertiser);
        return ResponseEntity.ok(createdAdvertiser);
    }

    /**
     * 更新广告主配置
     *
     * @param id 广告主配置ID
     * @param advertiserDetails 更新后的广告主配置对象
     * @return 更新成功的广告主配置对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<Advertiser> updateAdvertiser(
            @PathVariable Long id, 
            @RequestBody Advertiser advertiserDetails) {
        Advertiser updatedAdvertiser = advertiserService.updateAdvertiser(id, advertiserDetails);
        return ResponseEntity.ok(updatedAdvertiser);
    }

    /**
     * 删除广告主配置
     *
     * @param id 广告主配置ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAdvertiser(@PathVariable Long id) {
        advertiserService.deleteAdvertiser(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取广告主配置
     *
     * @param id 广告主配置ID
     * @return 获取到的广告主配置对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<Advertiser> getAdvertiserById(@PathVariable Long id) {
        return advertiserService.getAdvertiserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}