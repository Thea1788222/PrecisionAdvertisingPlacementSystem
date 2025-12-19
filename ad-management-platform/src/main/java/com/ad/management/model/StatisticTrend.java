package com.ad.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticTrend {
    private LocalDate date;
    private Long impressions = 0L;
    private Long clicks = 0L;
    private Double revenue = 0.0;

    public StatisticTrend(LocalDate date) {
        this.date = date;
    }
}