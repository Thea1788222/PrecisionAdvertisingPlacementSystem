package com.ad.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "ad_materials")
public class AdMaterial {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 广告主ID
    private Long advertiserId;

    // 标题
    private String title;

    // 类型
    private String type;

    // 图片URL
    private String imageUrl;

    // 视频URL
    private String videoUrl;

    // 链接URL
    private String linkUrl;

    // 宽度
    private Integer width;

    // 高度
    private Integer height;

    // 持续时间
    private Integer duration;

    // 分类
    private String category;

    // 目标兴趣
    private String targetInterest;

    // 竞价价格
    private BigDecimal bidPrice;

    // 状态
    private Integer status;

    // 创建时间
    private LocalDateTime createdAt;

}