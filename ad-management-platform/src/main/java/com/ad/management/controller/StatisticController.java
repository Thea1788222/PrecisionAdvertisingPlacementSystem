package com.ad.management.controller;

import com.ad.management.model.AdStatistic;
import com.ad.management.model.TrafficStatistic;
import com.ad.management.model.StatisticSummary;
import com.ad.management.model.StatisticTrend;
import com.ad.management.model.StatisticDistribution;
import com.ad.management.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    /**
     * 获取广告主广告统计数据
     *
     * @param adId     广告ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 广告主广告统计数据列表
     */
    @GetMapping("/ads")
    public ResponseEntity<List<AdStatistic>> getAdStatistics(
            @RequestParam(required = false) Long adId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<AdStatistic> statistics = statisticService.getAdStatistics(adId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取网站流量统计数据
     *
     * @param website   网站名称（可选）
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 网站流量统计数据列表
     */
    @GetMapping("/traffic")
    public ResponseEntity<List<TrafficStatistic>> getTrafficStatistics(
            @RequestParam(required = false) String website,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<TrafficStatistic> statistics = statisticService.getTrafficStatistics(website, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取统计摘要数据
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param website   网站名称（可选）
     * @return 统计摘要数据
     */
    @GetMapping("/summary")
    public ResponseEntity<StatisticSummary> getStatisticSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String website) {
        
        StatisticSummary summary = statisticService.getStatisticSummary(startDate, endDate, website);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 获取趋势统计数据
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param website   网站名称（可选）
     * @return 趋势统计数据列表
     */
    @GetMapping("/trends")
    public ResponseEntity<List<StatisticTrend>> getStatisticTrends(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String website) {
        
        List<StatisticTrend> trends = statisticService.getStatisticTrends(startDate, endDate, website);
        return ResponseEntity.ok(trends);
    }
    
    /**
     * 获取分布统计数据
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param dimension 维度（如：website, ad）
     * @param metric    指标（如：impressions, clicks, revenue）
     * @return 分布统计数据列表
     */
    @GetMapping("/distribution")
    public ResponseEntity<List<StatisticDistribution>> getStatisticDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String dimension,
            @RequestParam(required = false) String metric) {
        
        List<StatisticDistribution> distribution = statisticService.getStatisticDistribution(startDate, endDate, dimension, metric);
        return ResponseEntity.ok(distribution);
    }
}