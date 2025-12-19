package com.ad.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticSummary {
    private Long totalImpressions = 0L;
    private Long totalClicks = 0L;
    private Double totalRevenue = 0.0;
    private Double averageCtr = 0.0;
    private Double impressionsChange = 0.0;
    private Double clicksChange = 0.0;
    private Double revenueChange = 0.0;
    private Double ctrChange = 0.0;
}