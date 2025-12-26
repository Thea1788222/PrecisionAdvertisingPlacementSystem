package com.ad.tracker.controller;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.service.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")

public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    /**
     * 获取用户画像
     */
    @GetMapping("/profile/{userFingerprint}")
    public ResponseEntity<UserProfile> getUserProfile(
        @PathVariable String userFingerprint) {

        UserProfile profile = userProfileService.getUserProfileByFingerprint(userFingerprint);
        return ResponseEntity.ok(profile);
    }
    
    /**
     * 更新用户画像
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
        @RequestBody UserProfile userProfile) {

        userProfileService.saveUserProfile(userProfile);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户画像更新成功");
        return ResponseEntity.ok(response);
    }
}