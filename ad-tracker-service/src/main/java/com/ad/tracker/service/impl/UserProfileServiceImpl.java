package com.ad.tracker.service.impl;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserProfileRepository;
import com.ad.tracker.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * 根据用户指纹获取用户画像
     */
    public UserProfile getUserProfileByFingerprint(String fingerprint) {
        Optional<UserProfile> profile = userProfileRepository.findByUserFingerprint(fingerprint);
        return profile.orElse(null);
    }

    /**
     * 保存用户画像
     */
    public UserProfile saveUserProfile(UserProfile userProfile) {
        userProfile.setLastActive(LocalDateTime.now());
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }
        return userProfileRepository.save(userProfile);
    }
}