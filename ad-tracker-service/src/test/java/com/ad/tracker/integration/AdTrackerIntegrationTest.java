package com.ad.tracker.integration;

import com.ad.tracker.AdTrackerServiceApplication;
import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.AdMaterialRepository;
import com.ad.tracker.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AdTrackerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdTrackerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private AdMaterialRepository adMaterialRepository;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // 清理测试数据
        adMaterialRepository.deleteAll();
        userProfileRepository.deleteAll();
        
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        // 创建测试用户画像
        UserProfile userProfile = new UserProfile();
        userProfile.setCookieId("test-cookie-id");
        userProfile.setUserFingerprint("test-fingerprint");
        userProfile.setInterests("electronics,technology");
        userProfile.setCategories("shopping,tech");
        userProfile.setBehaviorScore(80);
        userProfileRepository.save(userProfile);
        
        // 创建测试广告素材
        AdMaterial adMaterial = new AdMaterial();
        adMaterial.setAdvertiserId(1L);
        adMaterial.setTitle("测试广告");
        adMaterial.setType("banner");
        adMaterial.setImageUrl("http://example.com/test-ad.jpg");
        adMaterial.setLinkUrl("http://example.com");
        adMaterial.setWidth(300);
        adMaterial.setHeight(250);
        adMaterial.setCategory("electronics");
        adMaterial.setBidPrice(new BigDecimal("1.50"));
        adMaterial.setStatus(1);
        adMaterialRepository.save(adMaterial);
    }

    @Test
    public void testFullAdTrackingFlow() {
        // 1. 测试记录用户行为
        testTrackUserBehavior();
        
        // 2. 测试获取推荐广告
        testGetRecommendedAds();
        
        // 3. 测试记录广告展示
        Long impressionId = testTrackAdImpression();
        
        // 4. 测试记录广告点击
        testTrackAdClick(impressionId);
        
        // 5. 测试用户画像更新
        testUpdateUserProfile();
    }

    private void testTrackUserBehavior() {
        Map<String, Object> behaviorData = new HashMap<>();
        behaviorData.put("userFingerprint", "test-fingerprint");
        behaviorData.put("cookieId", "test-cookie-id");
        behaviorData.put("website", "shopping-website");
        behaviorData.put("actionType", "page_view");
        behaviorData.put("targetId", "product-123");
        behaviorData.put("category", "electronics");
        behaviorData.put("keywords", "手机");
        behaviorData.put("duration", 30);
        behaviorData.put("ipAddress", "127.0.0.1");
        behaviorData.put("userAgent", "Mozilla/5.0");

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/track/behavior", behaviorData, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
    }

    private void testGetRecommendedAds() {
        Map<String, Object> recommendData = new HashMap<>();
        recommendData.put("cookieId", "test-cookie-id");
        recommendData.put("website", "shopping-website");
        recommendData.put("positions", new String[]{"top-banner"});
        recommendData.put("category", "electronics");
        recommendData.put("count", 5);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/ad/recommend", recommendData, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("ads");
    }

    private Long testTrackAdImpression() {
        Map<String, Object> impressionData = new HashMap<>();
        impressionData.put("adId", 1);
        impressionData.put("cookieId", "test-cookie-id");
        impressionData.put("website", "shopping-website");
        impressionData.put("position", "top-banner");
        impressionData.put("bidPrice", "1.50");

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/track/impression", impressionData, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
        assertThat(response.getBody()).containsKey("impressionId");
        
        return Long.valueOf(response.getBody().get("impressionId").toString());
    }

    private void testTrackAdClick(Long impressionId) {
        Map<String, Object> clickData = new HashMap<>();
        clickData.put("impressionId", impressionId);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/track/click", clickData, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
    }

    private void testUpdateUserProfile() {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("cookieId", "test-cookie-id");
        profileData.put("userFingerprint", "test-fingerprint");
        profileData.put("interests", "electronics,phones,technology");
        profileData.put("categories", "shopping,tech");
        profileData.put("behaviorScore", 85);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(profileData, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/api/user/profile", HttpMethod.PUT, entity, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
    }
}