package com.ad.tracker.service.impl;

import com.ad.tracker.model.AdImpression;
import com.ad.tracker.repository.AdImpressionRepository;
import com.ad.tracker.service.AdImpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class AdImpressionServiceImpl implements AdImpressionService {
    
    private static final Logger logger = Logger.getLogger(AdImpressionServiceImpl.class.getName());
    
    @Autowired
    private AdImpressionRepository adImpressionRepository;

    /**
     * 保存广告展示记录
     */
    public AdImpression saveAdImpression(Long adId, String userFingerprint, String website, 
                                       String position, BigDecimal bidPrice) {
        try {
            AdImpression impression = new AdImpression();
            impression.setAdId(adId);
            impression.setUserFingerprint(userFingerprint);
            impression.setWebsite(website);
            impression.setPosition(position);
            impression.setBidPrice(bidPrice);
            impression.setIsClicked(0); // 0表示未点击
            impression.setCreatedAt(LocalDateTime.now());
            return adImpressionRepository.save(impression);
        } catch (Exception e) {
            logger.severe("保存广告展示记录时出错: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 更新广告点击记录
     */
    public AdImpression updateAdClick(Long impressionId) {
        try {
            AdImpression impression = adImpressionRepository.findById(impressionId).orElse(null);
            if (impression != null) {
                impression.setIsClicked(1); // 1表示已点击
                return adImpressionRepository.save(impression);
            }
            return null;
        } catch (Exception e) {
            logger.severe("更新广告点击记录时出错: " + e.getMessage());
            throw e;
        }
    }
}