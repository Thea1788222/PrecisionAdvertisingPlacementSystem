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
@Table(name = "user_profiles")
public class UserProfile {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户指纹
    private String userFingerprint;

    // 用户兴趣
    private String interests;

    // 用户类别
    private String categories;

    // 行为得分
    private Integer behaviorScore;

    // 最后活跃时间
    private LocalDateTime lastActive;
    
    // 创建时间
    private LocalDateTime createdAt;

}