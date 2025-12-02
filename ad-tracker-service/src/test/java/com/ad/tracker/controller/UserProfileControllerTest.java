package com.ad.tracker.controller;

import com.ad.tracker.model.UserProfile;
import com.ad.tracker.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@ActiveProfiles("test")
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUserProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserFingerprint("test-fingerprint");
        profile.setInterests("test,interests");
        profile.setCategories("test,categories");
        profile.setBehaviorScore(80);

        when(userProfileService.getUserProfileByFingerprint("test-fingerprint")).thenReturn(profile);

        mockMvc.perform(get("/api/user/profile/test-fingerprint"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userFingerprint").value("test-fingerprint"));

        verify(userProfileService, times(1)).getUserProfileByFingerprint("test-fingerprint");
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUserFingerprint("test-fingerprint");
        profile.setInterests("test,interests");
        profile.setCategories("test,categories");
        profile.setBehaviorScore(80);

        mockMvc.perform(put("/api/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户画像更新成功"));

        verify(userProfileService, times(1)).saveUserProfile(any(UserProfile.class));
    }
}