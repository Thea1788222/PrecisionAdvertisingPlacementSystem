package com.ad.tracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad_impressions")
public class AdImpression {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ad_id")
    private Long adId;
    
    @Column(name = "cookie_id")
    private String cookieId;
    
    private String website;
    
    private String position;
    
    @Column(name = "is_clicked")
    private Integer isClicked;
    
    @Column(name = "bid_price")
    private BigDecimal bidPrice;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public AdImpression() {}
    
    public AdImpression(Long id, Long adId, String cookieId, String website, String position, 
                       Integer isClicked, BigDecimal bidPrice, LocalDateTime createdAt) {
        this.id = id;
        this.adId = adId;
        this.cookieId = cookieId;
        this.website = website;
        this.position = position;
        this.isClicked = isClicked;
        this.bidPrice = bidPrice;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAdId() {
        return adId;
    }
    
    public void setAdId(Long adId) {
        this.adId = adId;
    }
    
    public String getCookieId() {
        return cookieId;
    }
    
    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Integer getIsClicked() {
        return isClicked;
    }
    
    public void setIsClicked(Integer isClicked) {
        this.isClicked = isClicked;
    }
    
    public BigDecimal getBidPrice() {
        return bidPrice;
    }
    
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}