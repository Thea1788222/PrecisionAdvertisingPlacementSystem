package com.ad.management.controller;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.service.StatisticService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    
    private final StatisticService statisticService;
    
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }
    
    @GetMapping("/ads")
    public ResponseEntity<List<AdStatistic>> getAdStatistics(
            @RequestParam(required = false) Long adId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<AdStatistic> statistics = statisticService.getAdStatistics(adId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/traffic")
    public ResponseEntity<List<TrafficStatistic>> getTrafficStatistics(
            @RequestParam(required = false) String website,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<TrafficStatistic> statistics = statisticService.getTrafficStatistics(website, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}