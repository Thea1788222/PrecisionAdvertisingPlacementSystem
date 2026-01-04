package com.ad.management.service.impl;

import com.ad.management.model.entity.AdMaterial;
import com.ad.management.model.entity.AdStatistic;
import com.ad.management.model.entity.TrafficStatisticEntity;
import com.ad.management.model.entity.AdImpression;
import com.ad.management.model.entity.UserBehavior;
import com.ad.management.model.vo.StatisticSummary;
import com.ad.management.model.vo.StatisticTrend;
import com.ad.management.model.vo.StatisticDistribution;
import com.ad.management.model.vo.DashboardSummary;
import com.ad.management.repository.AdMaterialRepository;
import com.ad.management.repository.TrafficStatisticRepository;
import com.ad.management.repository.AdImpressionRepository;
import com.ad.management.repository.UserBehaviorRepository;
import com.ad.management.repository.AdPositionRepository;
import com.ad.management.repository.AdvertiserRepository;
import com.ad.management.service.StatisticService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Resource
    private AdMaterialRepository adMaterialRepository;
    @Resource
    private TrafficStatisticRepository trafficStatisticRepository;
    @Resource
    private AdImpressionRepository adImpressionRepository;
    @Resource
    private UserBehaviorRepository userBehaviorRepository;
    @Resource
    private AdPositionRepository adPositionRepository;
    @Resource
    private AdvertiserRepository advertiserRepository;
    
    /**
     * 获取广告统计信息
     *
     * @param adId      广告ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 广告统计信息列表
     */
    // TODO: 重新思考后端返回数据形式或前端展示方式
    @Override
    public List<AdStatistic> getAdStatistics(Long adId, LocalDate startDate, LocalDate endDate) {
        // 从ad_impressions表获取指定广告id和日期范围内的广告数据
        LocalDateTime startTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        List<AdImpression> impressions = adImpressionRepository.findByAdIdAndDateRange(adId, startTime, endTime);
        
        // 按广告ID和日期分组统计数据
        Map<Long, Map<LocalDate, AdStatistic>> statsMap = new HashMap<>();
        
        for (AdImpression impression : impressions) {
            Long currentAdId = impression.getAdId();
            LocalDate impressionDate = impression.getCreatedAt().toLocalDate();

            // 创建新的统计对象（如果不存在）
            statsMap.computeIfAbsent(currentAdId, k -> new TreeMap<>())
                    .computeIfAbsent(impressionDate, d -> createNewAdStatistic(currentAdId, d));
            
            AdStatistic stat = statsMap.get(currentAdId).get(impressionDate);
            stat.setImpressionsCount(stat.getImpressionsCount() + 1);
            if (impression.getIsClicked() != null && impression.getIsClicked() == 1) {
                stat.setClicksCount(stat.getClicksCount() + 1);
            }
            // 累加竞价价格作为成本
            if (impression.getBidPrice() != null) {
                stat.setCost(stat.getCost().add(impression.getBidPrice()));
            }
        }
        
        // 计算转化数据（从user_behaviors表）
        List<UserBehavior> behaviors = userBehaviorRepository.findAdClickBehaviorsByAdIdAndDateRange(adId, startTime, endTime);
        for (UserBehavior behavior : behaviors) {
            String targetId = behavior.getTargetId();
            if (targetId.startsWith("ad_")) {
                try {
                    Long behaviorAdId = Long.parseLong(targetId.substring(3)); // 去掉"ad_"前缀
                    LocalDate behaviorDate = behavior.getCreatedAt().toLocalDate();
                    
                    if (statsMap.containsKey(behaviorAdId) && statsMap.get(behaviorAdId).containsKey(behaviorDate)) {
                        AdStatistic stat = statsMap.get(behaviorAdId).get(behaviorDate);
                        stat.setConversionsCount(stat.getConversionsCount() + 1);
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的targetId
                }
            }
        }
        
        // 转换为列表并计算点击率
        List<AdStatistic> result = statsMap.values().stream()
                .flatMap(dateMap -> dateMap.values().stream())
                .collect(Collectors.toList());
        
        // 设置广告标题
        List<Long> adIds = result.stream()
                .map(AdStatistic::getAdId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> adTitleMap = new HashMap<>();
        if (!adIds.isEmpty()) {
            List<AdMaterial> adMaterials = adMaterialRepository.findAllById(adIds);
            adTitleMap = adMaterials.stream()
                    .collect(Collectors.toMap(AdMaterial::getId, AdMaterial::getTitle));
        }
        
        // 计算点击率并设置广告标题
        for (AdStatistic stat : result) {
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
        
        return result;
    }

    /**
     * 创建新的广告统计对象
     *
     * @param adId   广告ID
     * @param date   日期
     * @return 新的广告统计对象
     */
    private AdStatistic createNewAdStatistic(Long adId, LocalDate date) {
        AdStatistic stat = new AdStatistic();
        stat.setAdId(adId);
        stat.setDate(date);
        stat.setImpressionsCount(0);
        stat.setClicksCount(0);
        stat.setConversionsCount(0);
        stat.setCost(java.math.BigDecimal.ZERO);
        stat.setRevenue(java.math.BigDecimal.ZERO);
        stat.setCtr(0.0);
        return stat;
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
    public List<com.ad.management.model.vo.TrafficStatistic> getTrafficStatistics(String website, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        List<TrafficStatisticEntity> entities = trafficStatisticRepository.findByWebsiteAndDateRange(website, startTime, endTime);
        
        // Convert entity objects to DTO objects
        return entities.stream().map(entity -> {
            com.ad.management.model.vo.TrafficStatistic dto = new com.ad.management.model.vo.TrafficStatistic();
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
    // TODO: 按网站查询是否正确
    @Override
    public StatisticSummary getStatisticSummary(LocalDate startDate, LocalDate endDate, String website) {
        // 从ad_impressions表获取统计数据
        LocalDateTime startTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        List<AdImpression> impressions = adImpressionRepository.findByAdIdAndDateRange(null, startTime, endTime);
        
        // 计算总览数据
        long totalImpressions = impressions.size();
        long totalClicks = impressions.stream()
                .filter(impression -> impression.getIsClicked() != null && impression.getIsClicked() == 1)
                .count();
        
        double totalRevenue = impressions.stream()
                .filter(impression -> impression.getBidPrice() != null)
                .mapToDouble(impression -> impression.getBidPrice().doubleValue())
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
    // TODO: 按网站查询是否正确
    @Override
    public List<StatisticTrend> getStatisticTrends(LocalDate startDate, LocalDate endDate, String website) {
        // 从ad_impressions表获取数据
        LocalDateTime startTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        List<AdImpression> impressions = adImpressionRepository.findByAdIdAndDateRange(null, startTime, endTime);
        
        // 按日期分组并聚合数据
        Map<LocalDate, StatisticTrend> trendMap = new TreeMap<>();
        
        for (AdImpression impression : impressions) {
            LocalDate date = impression.getCreatedAt().toLocalDate();
            if (!trendMap.containsKey(date)) {
                trendMap.put(date, new StatisticTrend(date));
            }
            
            StatisticTrend trend = trendMap.get(date);
            trend.setImpressions(trend.getImpressions() + 1);
            if (impression.getIsClicked() != null && impression.getIsClicked() == 1) {
                trend.setClicks(trend.getClicks() + 1);
            }
            if (impression.getBidPrice() != null) {
                trend.setRevenue(trend.getRevenue() + impression.getBidPrice().doubleValue());
            }
        }
        
        return new ArrayList<>(trendMap.values());
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
    // TODO: 前端没有相关选择逻辑，是否需要按维度查询
    @Override
    public List<StatisticDistribution> getStatisticDistribution(LocalDate startDate, LocalDate endDate, String dimension, String metric) {
        // 从ad_impressions表获取数据
        LocalDateTime startTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        List<AdImpression> impressions = adImpressionRepository.findByAdIdAndDateRange(null, startTime, endTime);
        
        // 按网站进行分布统计
        Map<String, Double> distributionMap = new HashMap<>();
        
        switch (metric.toLowerCase()) {
            case "impressions":
                for (AdImpression impression : impressions) {
                    String site = impression.getWebsite() != null ? impression.getWebsite() : "unknown";
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 1);
                }
                break;
            case "clicks":
                for (AdImpression impression : impressions) {
                    if (impression.getIsClicked() != null && impression.getIsClicked() == 1) {
                        String site = impression.getWebsite() != null ? impression.getWebsite() : "unknown";
                        distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + 1);
                    }
                }
                break;
            case "revenue":
                for (AdImpression impression : impressions) {
                    String site = impression.getWebsite() != null ? impression.getWebsite() : "unknown";
                    double value = impression.getBidPrice() != null ? impression.getBidPrice().doubleValue() : 0.0;
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + value);
                }
                break;
            default:
                // 默认按收入统计
                for (AdImpression impression : impressions) {
                    String site = impression.getWebsite() != null ? impression.getWebsite() : "unknown";
                    double value = impression.getBidPrice() != null ? impression.getBidPrice().doubleValue() : 0.0;
                    distributionMap.put(site, distributionMap.getOrDefault(site, 0.0) + value);
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
    
    /**
     * 获取仪表盘摘要信息
     *
     * @return 仪表盘摘要信息
     */
    @Override
    public DashboardSummary getDashboardSummary() {
        // 获取广告素材总数
        Long materialCount = (long) adMaterialRepository.findAll().size();
        
        // 获取广告位总数
        Long positionCount = (long) adPositionRepository.findAll().size();
        
        // 获取广告商总数
        Long advertiserCount = (long) advertiserRepository.findAll().size();
        
        // 获取今日展示次数 - 从ad_impressions表获取
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(today, LocalTime.MAX);
        Long todayImpressions = adImpressionRepository.countImpressionsByAdIdAndDateRange(null, startTime, endTime);
        
        return new DashboardSummary(materialCount, positionCount, advertiserCount, todayImpressions);
    }
}