package com.ad.tracker.service;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.AdMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdRecommendationService {
    
    @Autowired
    private AdMaterialRepository adMaterialRepository;
    
    @Autowired
    private UserProfileService userProfileService;
    
    public List<AdMaterial> getRecommendedAds(String cookieId, String website, 
                                            List<String> positions, String category, int count) {
        // 获取用户画像
        UserProfile userProfile = userProfileService.getUserProfileByCookieId(cookieId);
        
        // 根据类别获取广告素材
        List<AdMaterial> materials = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                category, 1); // 1表示启用状态
        }
        
        // 如果指定了数量，截取相应数量的广告
        if (count > 0 && materials.size() > count) {
            materials = materials.subList(0, count);
        }
        
        return materials;
    }
}