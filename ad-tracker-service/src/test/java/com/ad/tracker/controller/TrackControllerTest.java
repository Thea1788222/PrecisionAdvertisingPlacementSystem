package com.ad.tracker.controller;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.AdImpression;
import com.ad.tracker.service.impl.AdImpressionServiceImpl;
import com.ad.tracker.service.impl.UserBehaviorServiceImpl;
import com.ad.tracker.service.impl.AdRecommendationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackController.class)
@ActiveProfiles("test")
public class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBehaviorServiceImpl userBehaviorService;

    @MockBean
    private AdImpressionServiceImpl adImpressionService;

    @MockBean
    private AdRecommendationServiceImpl adRecommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTrackBehavior() throws Exception {
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUserFingerprint("test-fingerprint");
        userBehavior.setWebsite("test-website");
        userBehavior.setActionType("page_view");
        userBehavior.setTargetId("test-target-id");
        userBehavior.setCategory("test-category");

        when(userBehaviorService.saveUserBehavior(any(UserBehavior.class))).thenReturn(userBehavior);

        mockMvc.perform(post("/api/track/behavior")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userBehavior)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("行为记录成功"));

        verify(userBehaviorService, times(1)).saveUserBehavior(any(UserBehavior.class));
    }

    @Test
    void testTrackImpression() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("adId", 100);
        request.put("userFingerprint", "test-fingerprint");
        request.put("website", "test-website");
        request.put("position", "top-banner");
        request.put("bidPrice", "1.50");

        AdImpression impression = new AdImpression();
        impression.setId(1L);
        impression.setAdId(100L);
        impression.setUserFingerprint("test-fingerprint");
        impression.setWebsite("test-website");
        impression.setPosition("top-banner");
        impression.setBidPrice(new BigDecimal("1.50"));
        impression.setIsClicked(0);

        when(adImpressionService.saveAdImpression(anyLong(), anyString(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(impression);

        mockMvc.perform(post("/api/track/impression")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.impressionId").value(1))
                .andExpect(jsonPath("$.message").value("展示记录成功"));

        verify(adImpressionService, times(1)).saveAdImpression(anyLong(), anyString(), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void testTrackClick() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("impressionId", 1);
        request.put("userFingerprint", "test-fingerprint");

        mockMvc.perform(post("/api/track/click")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("点击记录成功"));

        verify(adImpressionService, times(1)).updateAdClick(anyLong());
    }
}