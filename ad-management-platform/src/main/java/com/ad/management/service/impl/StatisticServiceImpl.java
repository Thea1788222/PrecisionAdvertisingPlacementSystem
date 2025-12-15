package com.ad.management.service.impl;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.TrafficStatisticEntity;
import com.ad.management.repository.AdStatisticRepository;
import com.ad.management.repository.TrafficStatisticRepository;
import com.ad.management.service.StatisticService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {
    
    private final AdStatisticRepository adStatisticRepository;
    private final TrafficStatisticRepository trafficStatisticRepository;
    
    public StatisticServiceImpl(AdStatisticRepository adStatisticRepository, 
                           TrafficStatisticRepository trafficStatisticRepository) {
        this.adStatisticRepository = adStatisticRepository;
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
        return adStatisticRepository.findByAdIdAndDateRange(adId, startDate, endDate);
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