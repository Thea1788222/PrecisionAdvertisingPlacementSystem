package com.ad.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user_behaviors")
public class UserBehavior {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户指纹
    private String userFingerprint;

    // 网站名称
    private String website;
    
    // 行为类型
    private String actionType;

    // 目标ID
    private String targetId;

    // 类别
    private String category;

    // 关键字
    private String keywords;

    // 持续时间
    private Integer duration;

    // IP地址
    private String ipAddress;

    // 用户代理
    private String userAgent;

    // 创建时间
    private LocalDateTime createdAt;

}