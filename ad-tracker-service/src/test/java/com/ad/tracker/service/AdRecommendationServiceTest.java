package com.ad.tracker.service;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.AdMaterialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdRecommendationServiceTest {

    @Autowired
    private AdRecommendationService adRecommendationService;

    @MockBean
    private AdMaterialRepository adMaterialRepository;
    
    @MockBean
    private UserProfileService userProfileService;

    @Test
    void testGetRecommendedAds() {
        // 准备测试数据
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setCookieId("test-cookie-id");
        profile.setUserFingerprint("test-fingerprint");
        profile.setInterests("test,interests");
        profile.setCategories("test,categories");
        profile.setBehaviorScore(80);
        
        List<AdMaterial> materials = new ArrayList<>();
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
        materials.add(material);
        
        // 设置mock行为
        when(userProfileService.getUserProfileByCookieId("test-cookie-id")).thenReturn(profile);
        when(adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc("test", 1)).thenReturn(materials);
        
        // 调用被测试方法
        List<AdMaterial> result = adRecommendationService.getRecommendedAds(
            "test-cookie-id", "test-website", List.of("top-banner"), "test", 5);
        
        // 验证结果
        assert result.size() == 1;
        assert result.get(0).getId().equals(1L);
        assert result.get(0).getTitle().equals("Test Ad");
        
        verify(userProfileService, times(1)).getUserProfileByCookieId("test-cookie-id");
        verify(adMaterialRepository, times(1)).findByCategoryAndStatusOrderByBidPriceDesc("test", 1);
    }
}