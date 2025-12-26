package com.ad.management.service.impl;

import com.ad.management.model.AdMaterial;
import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.TrafficStatisticEntity;
import com.ad.management.model.StatisticSummary;
import com.ad.management.model.StatisticTrend;
import com.ad.management.model.StatisticDistribution;
import com.ad.management.repository.AdMaterialRepository;
import com.ad.management.repository.AdStatisticRepository;
import com.ad.management.repository.TrafficStatisticRepository;
import com.ad.management.service.StatisticService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {
    
    private final AdStatisticRepository adStatisticRepository;
    private final AdMaterialRepository adMaterialRepository;
    private final TrafficStatisticRepository trafficStatisticRepository;
    
    public StatisticServiceImpl(AdStatisticRepository adStatisticRepository, 
                               AdMaterialRepository adMaterialRepository,
                               TrafficStatisticRepository trafficStatisticRepository) {
        this.adStatisticRepository = adStatisticRepository;
        this.adMaterialRepository = adMaterialRepository;
        this.trafficStatisticRepository = trafficStatisticRepository;
    }
    
    /**
     * 获取广告统计信息
     *
     * @param adId      广告ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 广告统计信息列表
     */
    @Override
    public List<AdStatistic> getAdStatistics(Long adId, LocalDate startDate, LocalDate endDate) {
        List<AdStatistic> statistics = adStatisticRepository.findByAdIdAndDateRange(adId, startDate, endDate);
        
        // 获取所有相关的广告素材
        List<Long> adIds = statistics.stream()
                .map(AdStatistic::getAdId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> adTitleMap = new HashMap<>();
        if (!adIds.isEmpty()) {
            List<AdMaterial> adMaterials = adMaterialRepository.findAllById(adIds);
            adTitleMap = adMaterials.stream()
                    .collect(Collectors.toMap(AdMaterial::getId, AdMaterial::getTitle));
        }
        
        // 计算点击率并设置广告标题
        for (AdStatistic stat : statistics) {
            // 计算点击率
            if (stat.getImpressionsCount() != null && stat.getImpressionsCount() > 0) {
                double ctr = (double) stat.getClicksCount() / stat.getImpressionsCount();
                stat.setCtr(ctr);
            } else {
                stat.setCtr(0.0);
            }
            
            // 设置广告标题
            if (stat.getAdId() != null) {
                stat.setAdTitle(adTitleMap.getOrDefault(stat.getAdId(), "未知广告"));
            }
        }
        
        return statistics;
    }

    /**
     * 获取流量统计信息
     *
     * @param website   网站名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 流量统计信息列表
     */
    @Override
    public List<TrafficStatistic> getTrafficStatistics(String website, LocalDate startDate, LocalDate endDate) {
        List<TrafficStatisticEntity> entities = trafficStatisticRepository.findByWebsiteAndDateRange(website, startDate, endDate);
        
        // Convert entity objects to DTO objects
        return entities.stream().map(entity -> {
            TrafficStatistic dto = new TrafficStatistic();
            dto.setDate(entity.getDate());
            dto.setWebsite(entity.getWebsite());
            dto.setVisits(entity.getVisits());
            dto.setUniqueVisitors(entity.getUniqueVisitors());
            dto.setPageViews(entity.getPageViews());
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取统计摘要信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param website   网站名称
     * @return 统计摘要信息
     */
    @Override
    public StatisticSummary getStatisticSummary(LocalDate startDate, LocalDate endDate, String website) {
        // 获取广告统计数据
        List<AdStatistic> adStatistics = adStatisticRepository.findByAdIdAndDateRange(null, startDate, endDate);
        
        // 计算总览数据
        long totalImpressions = adStatistics.stream()
                .mapToLong(stat -> stat.getImpressionsCount() != null ? stat.getImpressionsCount() : 0)
                .sum();
                
        long totalClicks = adStatistics.stream()
                .mapToLong(stat -> stat.getClicksCount() != null ? stat.getClicksCount() : 0)
                .sum();
                
        double totalRevenue = adStatistics.stream()
                .mapToDouble(stat -> stat.getRevenue() != null ? stat.getRevenue().doubleValue() : 0.0)
                .sum();
                
        double averageCtr = totalImpressions > 0 ? (double) totalClicks / totalImpressions : 0.0;
        
        // 创建统计摘要对象
        StatisticSummary summary = new StatisticSummary();
        summary.setTotalImpressions(totalImpressions);
        summary.setTotalClicks(totalClicks);
        summary.setTotalRevenue(totalRevenue);
        summary.setAverageCtr(averageCtr * 100); // 转换为百分比
        
        // 简化变化率计算（实际应用中应该比较两个时间段的数据）
        summary.setImpressionsChange(0.0);
        summary.setClicksChange(0.0);
        summary.setRevenueChange(0.0);
        summary.setCtrChange(0.0);
        
        return summary;
    }
    
    /**
     * 获取趋势统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param website   网站名称
     * @return 趋势统计数据列表
     */
    @Override
    public List<StatisticTrend> getStatisticTrends(LocalDate startDate, LocalDate endDate, String website) {
        // 获取广告统计数据
        List<AdStatistic> adStatistics = adStatisticRepository.findByAdIdAndDateRange(null, startDate, endDate);
        
        // 按日期分组并聚合数据
        Map<LocalDate, StatisticTrend> trendMap = new HashMap<>();
        
        for (AdStatistic stat : adStatistics) {
            LocalDate date = stat.getDate();
            if (!trendMap.containsKey(date)) {
                trendMap.put(date, new StatisticTrend(date));
            }
            
            StatisticTrend trend = trendMap.get(date);
            trend.setImpressions(trend.getImpressions() + (stat.getImpressionsCount() != null ? stat.getImpressionsCount() : 0));
            trend.setClicks(trend.getClicks() + (stat.getClicksCount() != null ? stat.getClicksCount() : 0));
            trend.setRevenue(trend.getRevenue() + (stat.getRevenue() != null ? stat.getRevenue().doubleValue() : 0.0));
        }
        
        return trendMap.values().stream().collect(Collectors.toList());
    }
    
    /**
     * 获取分布统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param dimension 维度
     * @param metric    指标
     * @return 分布统计数据列表
     */
    @Override
    public List<StatisticDistribution> getStatisticDistribution(LocalDate startDate, LocalDate endDate, String dimension, String metric) {
        // 获取广告统计数据
        List<AdStatistic> adStatistics = adStatisticRepository.findByAdIdAndDateRange(null, startDate, endDate);
        
        // 这里我们简化处理，只按网站维度进行分布统计
        // 实际应用中可以从广告信息中获取网站信息
        Map<String, Double> distributionMap = new HashMap<>();
        
        switch (metric.toLowerCase()) {
            case "impressions":
                for (AdStatistic stat : adStatistics) {
                    String site = "unknown"; // 简化处理，实际应从广告信息中获取网站
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 
                            (stat.getImpressionsCount() != null ? stat.getImpressionsCount() : 0));
                }
                break;
            case "clicks":
                for (AdStatistic stat : adStatistics) {
                    String site = "unknown"; // 简化处理，实际应从广告信息中获取网站
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 
                            (stat.getClicksCount() != null ? stat.getClicksCount() : 0));
                }
                break;
            case "revenue":
                for (AdStatistic stat : adStatistics) {
                    String site = "unknown"; // 简化处理，实际应从广告信息中获取网站
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 
                            (stat.getRevenue() != null ? stat.getRevenue().doubleValue() : 0.0));
                }
                break;
            default:
                // 默认按收入统计
                for (AdStatistic stat : adStatistics) {
                    String site = "unknown"; // 简化处理，实际应从广告信息中获取网站
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 
                            (stat.getRevenue() != null ? stat.getRevenue().doubleValue() : 0.0));
                }
        }
        
        // 计算总数
        double total = distributionMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // 转换为分布数据列表
        return distributionMap.entrySet().stream()
                .map(entry -> {
                    StatisticDistribution dist = new StatisticDistribution();
                    dist.setDimension(entry.getKey());
                    dist.setMetricValue(entry.getValue());
                    dist.setPercentage(total > 0 ? (entry.getValue() / total) * 100 : 0.0);
                    return dist;
                })
                .collect(Collectors.toList());
    }
}