package com.ad.tracker.controller;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户画像", description = "用户画像管理相关接口")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    /**
     * 获取用户画像
     */
    @GetMapping("/profile/{cookieId}")
    @Operation(summary = "获取用户画像", description = "根据Cookie ID获取用户画像信息")
    @ApiResponse(responseCode = "200", description = "用户画像获取成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = UserProfile.class)))
    public ResponseEntity<UserProfile> getUserProfile(
        @Parameter(description = "用户Cookie ID") 
        @PathVariable String cookieId) {
        UserProfile profile = userProfileService.getUserProfileByCookieId(cookieId);
        return ResponseEntity.ok(profile);
    }
    
    /**
     * 更新用户画像
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户画像", description = "更新用户画像信息")
    @ApiResponse(responseCode = "200", description = "用户画像更新成功", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> updateUserProfile(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户画像信息") 
        @RequestBody UserProfile userProfile) {
        userProfileService.saveUserProfile(userProfile);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户画像更新成功");
        return ResponseEntity.ok(response);
    }
}