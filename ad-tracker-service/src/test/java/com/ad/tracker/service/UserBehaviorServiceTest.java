package com.ad.tracker.service;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.repository.UserBehaviorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserBehaviorServiceTest {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @MockBean
    private UserBehaviorRepository userBehaviorRepository;

    @Test
    void testSaveUserBehavior() {
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUserFingerprint("test-fingerprint");
        userBehavior.setCookieId("test-cookie-id");
        userBehavior.setWebsite("test-website");
        userBehavior.setActionType("page_view");
        userBehavior.setTargetId("test-target-id");
        userBehavior.setCategory("test-category");
        
        when(userBehaviorRepository.save(any(UserBehavior.class))).thenReturn(userBehavior);
        
        userBehaviorService.saveUserBehavior(userBehavior);
        
        verify(userBehaviorRepository, times(1)).save(userBehavior);
    }
}