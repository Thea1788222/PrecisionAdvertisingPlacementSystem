package com.ad.management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ad_statistics")
public class AdStatistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ad_id")
    private Long adId;
    
    // 添加广告标题字段，用于前端显示
    @Transient
    private String adTitle;
    
    @Column(name = "impressions_count")
    private Integer impressionsCount = 0;
    
    @Column(name = "clicks_count")
    private Integer clicksCount = 0;
    
    @Column(name = "conversions_count")
    private Integer conversionsCount = 0;
    
    private BigDecimal cost = BigDecimal.ZERO;
    
    private BigDecimal revenue = BigDecimal.ZERO;
    
    // 点击率
    private Double ctr = 0.0;
    
    private LocalDate date;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}