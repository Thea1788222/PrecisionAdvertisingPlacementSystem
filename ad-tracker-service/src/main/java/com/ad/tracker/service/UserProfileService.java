package com.ad.tracker.service;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public UserProfile getUserProfileByCookieId(String cookieId) {
        Optional<UserProfile> profile = userProfileRepository.findByCookieId(cookieId);
        return profile.orElse(null);
    }
    
    public UserProfile saveUserProfile(UserProfile userProfile) {
        userProfile.setLastActive(LocalDateTime.now());
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }
        return userProfileRepository.save(userProfile);
    }
}