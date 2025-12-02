package com.ad.tracker.service;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserProfileServiceTest {

    @Autowired
    private UserProfileService userProfileService;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @Test
    void testGetUserProfileByFingerprint() {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserFingerprint("test-fingerprint");
        profile.setInterests("test,interests");
        profile.setCategories("test,categories");
        profile.setBehaviorScore(80);
        
        when(userProfileRepository.findByUserFingerprint("test-fingerprint")).thenReturn(Optional.of(profile));
        
        UserProfile result = userProfileService.getUserProfileByFingerprint("test-fingerprint");
        
        verify(userProfileRepository, times(1)).findByUserFingerprint("test-fingerprint");
    }
    
    @Test
    void testSaveUserProfile() {
        UserProfile profile = new UserProfile();
        profile.setUserFingerprint("test-fingerprint");
        profile.setInterests("test,interests");
        profile.setCategories("test,categories");
        profile.setBehaviorScore(80);
        
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        
        UserProfile result = userProfileService.saveUserProfile(profile);
        
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }
}