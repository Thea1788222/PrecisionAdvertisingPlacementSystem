package com.ad.management.service;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import java.time.LocalDate;
import java.util.List;

public interface StatisticService {
    
    /**
     * 获取广告统计信息
     *
     * @param adId      广告ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 广告统计信息列表
     */
    List<AdStatistic> getAdStatistics(Long adId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取流量统计信息
     *
     * @param website   网站名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 流量统计信息列表
     */
    List<TrafficStatistic> getTrafficStatistics(String website, LocalDate startDate, LocalDate endDate);
}