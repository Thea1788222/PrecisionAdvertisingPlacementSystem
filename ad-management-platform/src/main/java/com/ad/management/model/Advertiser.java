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
@Table(name = "advertisers")
public class Advertiser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String contact;
    
    @Column(unique = true)
    private String email;
    
    private BigDecimal balance = BigDecimal.ZERO;
    
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