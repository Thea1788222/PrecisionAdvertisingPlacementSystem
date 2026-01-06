package com.ad.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ad_impressions")
public class AdImpression {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 广告ID
    private Long adId;

    // 用户指纹
    private String userFingerprint;

    // 网站
    private String website;

    // 广告位置
    private String position;

    // 是否点击
    private Integer isClicked;

    // 竞标价格
    private BigDecimal bidPrice;

    // 创建时间
    private LocalDateTime createdAt;
}