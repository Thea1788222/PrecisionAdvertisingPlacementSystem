package com.ad.tracker.service.impl;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserBehaviorRepository;
import com.ad.tracker.repository.UserProfileRepository;
import com.ad.tracker.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * 保存用户行为数据
     */
    public UserBehavior saveUserBehavior(UserBehavior userBehavior) {
        // 设置创建时间
        userBehavior.setCreatedAt(LocalDateTime.now());

        // 保存用户行为数据
        UserBehavior savedBehavior = userBehaviorRepository.save(userBehavior);
        
        // 更新或创建用户画像
        updateUserProfile(userBehavior);
        
        return savedBehavior;
    }

    /**
     * 更新用户画像
     */
    private void updateUserProfile(UserBehavior userBehavior) {
        // 查找现有用户画像
        UserProfile userProfile = userProfileRepository.findByUserFingerprint(userBehavior.getUserFingerprint())
                .orElse(new UserProfile());
        
        // 如果是新用户画像，设置基本属性
        if (userProfile.getId() == null) {
            userProfile.setUserFingerprint(userBehavior.getUserFingerprint());
            userProfile.setInterests(userBehavior.getCategory());
            userProfile.setCategories(userBehavior.getCategory());
            userProfile.setBehaviorScore(10); // 初始分数
        } else {
            // 更新现有用户画像
            // 增加行为分数并更新兴趣和类别
            int currentScore = userProfile.getBehaviorScore() != null ? userProfile.getBehaviorScore() : 0;
            userProfile.setBehaviorScore(currentScore + 5);
            
            // 更新兴趣
            String currentInterests = userProfile.getInterests();
            if (currentInterests == null || currentInterests.isEmpty()) {
                userProfile.setInterests(userBehavior.getCategory());
            } else if (!currentInterests.contains(userBehavior.getCategory())) {
                userProfile.setInterests(currentInterests + "," + userBehavior.getCategory());
            }

            // 更新类别
            String currentCategories = userProfile.getCategories();
            if (currentCategories == null || currentCategories.isEmpty()) {
                userProfile.setCategories(userBehavior.getCategory());
            } else if (!currentCategories.contains(userBehavior.getCategory())) {
                userProfile.setCategories(currentCategories + "," + userBehavior.getCategory());
            }
        }
        
        userProfile.setLastActive(LocalDateTime.now());
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }

        // 保存用户画像
        userProfileRepository.save(userProfile);
    }
}