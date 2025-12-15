package com.ad.tracker.service;


import com.ad.tracker.model.AdImpression;

import java.math.BigDecimal;

/**
 * @author DaYang
 * @date 2025/12/10
 * @description
 */
public interface AdImpressionService {
    /**
     * 保存广告展示记录
     * @param adId 广告ID
     * @param userFingerprint 用户指纹
     * @param website 网站
     * @param position 广告位置
     * @param bidPrice 竞价价格
     * @return 广告展示记录
     */
    public AdImpression saveAdImpression(Long adId, String userFingerprint, String website, String position, BigDecimal bidPrice);

    /**
     * 更新广告点击记录
     * @param impressionId 广告展示记录ID
     * @return 更新后的广告点击记录
     */
    public AdImpression updateAdClick(Long impressionId);
}
