package com.ad.management.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private Long materialCount = 0L;
    private Long positionCount = 0L;
    private Long advertiserCount = 0L;
    private Long todayImpressions = 0L;
}