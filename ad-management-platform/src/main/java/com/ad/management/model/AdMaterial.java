package com.ad.management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ad_materials")
public class AdMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "advertiser_id", nullable = false)
    private Long advertiserId;
    
    @Column(nullable = false)
    private String title;
    
    // 广告类型(banner, video, native)
    @Column(nullable = false)
    private String type;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "link_url")
    private String linkUrl;
    
    private Integer width;
    
    private Integer height;
    
    private Integer duration;
    
    private String category;
    
    @Column(name = "target_interest")
    private String targetInterest;
    
    @Column(name = "bid_price")
    private BigDecimal bidPrice;
    
    // 状态(1:启用, 0:禁用)
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Byte status = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}