package com.ad.management.service.impl;

import com.ad.management.model.AdMaterial;
import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.TrafficStatisticEntity;
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
}