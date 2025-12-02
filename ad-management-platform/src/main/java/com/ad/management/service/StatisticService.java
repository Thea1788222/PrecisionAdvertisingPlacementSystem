package com.ad.management.service;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.TrafficStatisticEntity;
import com.ad.management.repository.AdStatisticRepository;
import com.ad.management.repository.TrafficStatisticRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    
    private final AdStatisticRepository adStatisticRepository;
    private final TrafficStatisticRepository trafficStatisticRepository;
    
    public StatisticService(AdStatisticRepository adStatisticRepository, 
                           TrafficStatisticRepository trafficStatisticRepository) {
        this.adStatisticRepository = adStatisticRepository;
        this.trafficStatisticRepository = trafficStatisticRepository;
    }
    
    public List<AdStatistic> getAdStatistics(Long adId, LocalDate startDate, LocalDate endDate) {
        return adStatisticRepository.findByAdIdAndDateRange(adId, startDate, endDate);
    }
    
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