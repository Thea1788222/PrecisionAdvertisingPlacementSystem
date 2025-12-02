package com.ad.tracker.controller;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.service.AdRecommendationService;
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

@WebMvcTest(AdRecommendationController.class)
@ActiveProfiles("test")
public class AdRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdRecommendationService adRecommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRecommendedAds() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("userFingerprint", "test-fingerprint");
        request.put("website", "test-website");
        request.put("positions", Arrays.asList("top-banner", "sidebar"));
        request.put("category", "test");
        request.put("count", 5);

        List<AdMaterial> ads = new ArrayList<>();
        AdMaterial material = new AdMaterial();
        material.setId(1L);
        material.setAdvertiserId(100L);
        material.setTitle("Test Ad");
        material.setType("banner");
        material.setImageUrl("http://example.com/image.jpg");
        material.setLinkUrl("http://example.com");
        material.setWidth(300);
        material.setHeight(250);
        material.setCategory("test");
        material.setBidPrice(new BigDecimal("1.50"));
        material.setStatus(1);
        ads.add(material);

        when(adRecommendationService.getRecommendedAds(anyString(), anyString(), anyList(), anyString(), anyInt()))
                .thenReturn(ads);

        mockMvc.perform(post("/api/ad/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ads").isArray())
                .andExpect(jsonPath("$.ads[0].id").value(1))
                .andExpect(jsonPath("$.ads[0].title").value("Test Ad"));

        verify(adRecommendationService, times(1)).getRecommendedAds(anyString(), anyString(), anyList(), anyString(), anyInt());
    }
}