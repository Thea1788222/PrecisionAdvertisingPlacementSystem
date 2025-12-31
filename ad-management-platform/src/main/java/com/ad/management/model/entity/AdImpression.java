package com.ad.management.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad_impressions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdImpression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad_id")
    private Long adId;

    @Column(name = "user_fingerprint")
    private String userFingerprint;

    private String website;

    private String position;

    @Column(name = "is_clicked")
    private Integer isClicked;

    @Column(name = "bid_price")
    private BigDecimal bidPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}