package com.ad.tracker.service;

import com.ad.tracker.model.AdImpression;
import com.ad.tracker.repository.AdImpressionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdImpressionServiceTest {

    @Autowired
    private AdImpressionService adImpressionService;

    @MockBean
    private AdImpressionRepository adImpressionRepository;

    @Test
    void testSaveAdImpression() {
        AdImpression impression = new AdImpression();
        impression.setId(1L);
        impression.setAdId(100L);
        impression.setUserFingerprint("test-fingerprint");
        impression.setWebsite("test-website");
        impression.setPosition("top-banner");
        impression.setBidPrice(new BigDecimal("1.50"));
        impression.setIsClicked(0);
        
        when(adImpressionRepository.save(any(AdImpression.class))).thenReturn(impression);
        
        AdImpression result = adImpressionService.saveAdImpression(
            100L, "test-fingerprint", "test-website", "top-banner", new BigDecimal("1.50"));
        
        verify(adImpressionRepository, times(1)).save(any(AdImpression.class));
    }
    
    @Test
    void testUpdateAdClick() {
        AdImpression impression = new AdImpression();
        impression.setId(1L);
        impression.setAdId(100L);
        impression.setUserFingerprint("test-fingerprint");
        impression.setWebsite("test-website");
        impression.setPosition("top-banner");
        impression.setBidPrice(new BigDecimal("1.50"));
        impression.setIsClicked(0);
        
        when(adImpressionRepository.findById(1L)).thenReturn(Optional.of(impression));
        when(adImpressionRepository.save(any(AdImpression.class))).thenReturn(impression);
        
        AdImpression result = adImpressionService.updateAdClick(1L);
        
        verify(adImpressionRepository, times(1)).findById(1L);
        verify(adImpressionRepository, times(1)).save(any(AdImpression.class));
    }
}