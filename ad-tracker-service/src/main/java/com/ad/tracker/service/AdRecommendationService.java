package com.ad.tracker.service;


import com.ad.tracker.model.AdMaterial;

import java.util.List;

/**
 * @author DaYang
 * @date 2025/12/10
 * @description
 */
public interface AdRecommendationService {

    /**
     * 获取推荐广告
     *
     * @param userFingerprint 用户指纹
     * @param website         网站地址
     * @param positions       广告位置
     * @param category        广告类别
     * @param count           数量
     * @return 推荐广告列表
     */
    public List<AdMaterial> getRecommendedAds(String userFingerprint, String website,
                                              List<String> positions, String category, String type, int count);

}
