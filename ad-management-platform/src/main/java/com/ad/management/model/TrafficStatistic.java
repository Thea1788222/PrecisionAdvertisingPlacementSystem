package com.ad.management.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficStatistic {
    private LocalDate date;
    private String website;
    private Integer visits = 0;
    private Integer uniqueVisitors = 0;
    private Integer pageViews = 0;
}