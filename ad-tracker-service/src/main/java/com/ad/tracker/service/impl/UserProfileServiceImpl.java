package com.ad.tracker.service.impl;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserProfileRepository;
import com.ad.tracker.service.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据用户指纹获取用户画像
     */
    @Override
    public UserProfile getUserProfileByFingerprint(String userFingerprint) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUserFingerprint(userFingerprint);
        return userProfileOpt.orElse(null);
    }

    @Override
    public UserProfile saveUserProfile(UserProfile userProfile) {
        return null;
    }

    /**
     * 解析兴趣JSON字符串为Map
     */
    public Map<String, Double> parseInterests(UserProfile userProfile) {
        if (userProfile == null || userProfile.getInterests() == null || userProfile.getInterests().equals("{}")) {
            return new HashMap<>();
        }
        
        try {
            // 使用ObjectMapper解析JSON字符串为Map
            return objectMapper.readValue(userProfile.getInterests(), new TypeReference<Map<String, Double>>() {});
        } catch (JsonProcessingException e) {
            // 如果解析失败，返回空Map
            return new HashMap<>();
        }
    }

    /**
     * 解析类别JSON字符串为List
     */
    public List<String> parseCategories(UserProfile userProfile) {
        if (userProfile == null || userProfile.getCategories() == null || userProfile.getCategories().equals("{}")) {
            return new ArrayList<>();
        }
        
        try {
            // 使用ObjectMapper解析JSON字符串为Map
            Map<String, Object> categoriesMap = objectMapper.readValue(userProfile.getCategories(), new TypeReference<Map<String, Object>>() {});
            return new ArrayList<>(categoriesMap.keySet());
        } catch (JsonProcessingException e) {
            // 如果解析失败，返回空List
            return new ArrayList<>();
        }
    }

    /**
     * 获取用户最高权重的兴趣类别
     */
    public String getTopInterestCategory(UserProfile userProfile) {
        Map<String, Double> interests = parseInterests(userProfile);
        if (interests.isEmpty()) {
            return null;
        }
        
        return interests.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 获取用户前N个兴趣类别
     */
    public List<String> getTopNInterestCategories(UserProfile userProfile, int n) {
        Map<String, Double> interests = parseInterests(userProfile);
        if (interests.isEmpty()) {
            return new ArrayList<>();
        }
        
        return interests.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }
}