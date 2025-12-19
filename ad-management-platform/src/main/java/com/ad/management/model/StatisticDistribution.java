package com.ad.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDistribution {
    private String dimension;
    private Double metricValue = 0.0;
    private Double percentage = 0.0;
}