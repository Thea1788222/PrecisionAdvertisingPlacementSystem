package com.ad.management.service;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.StatisticSummary;
import com.ad.management.model.StatisticTrend;
import com.ad.management.model.StatisticDistribution;
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
    
    /**
     * 获取统计摘要信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param website   网站名称
     * @return 统计摘要信息
     */
    StatisticSummary getStatisticSummary(LocalDate startDate, LocalDate endDate, String website);
    
    /**
     * 获取趋势统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param website   网站名称
     * @return 趋势统计数据列表
     */
    List<StatisticTrend> getStatisticTrends(LocalDate startDate, LocalDate endDate, String website);
    
    /**
     * 获取分布统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param dimension 维度
     * @param metric    指标
     * @return 分布统计数据列表
     */
    List<StatisticDistribution> getStatisticDistribution(LocalDate startDate, LocalDate endDate, String dimension, String metric);
}