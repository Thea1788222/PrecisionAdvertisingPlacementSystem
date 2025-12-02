package com.ad.management.controller;

import com.ad.management.model.Advertiser;
import com.ad.management.service.AdvertiserService;
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
    
    private final AdvertiserService advertiserService;
    
    public AdvertiserController(AdvertiserService advertiserService) {
        this.advertiserService = advertiserService;
    }
    
    @GetMapping
    public ResponseEntity<Page<Advertiser>> getAllAdvertisers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Advertiser> advertisers = advertiserService.getAllAdvertisers(name, pageable);
        return ResponseEntity.ok(advertisers);
    }
    
    @PostMapping
    public ResponseEntity<Advertiser> createAdvertiser(@RequestBody Advertiser advertiser) {
        Advertiser createdAdvertiser = advertiserService.createAdvertiser(advertiser);
        return ResponseEntity.ok(createdAdvertiser);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Advertiser> updateAdvertiser(
            @PathVariable Long id, 
            @RequestBody Advertiser advertiserDetails) {
        Advertiser updatedAdvertiser = advertiserService.updateAdvertiser(id, advertiserDetails);
        return ResponseEntity.ok(updatedAdvertiser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdvertiser(@PathVariable Long id) {
        advertiserService.deleteAdvertiser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "广告商删除成功");
        return ResponseEntity.ok(response);
    }
}