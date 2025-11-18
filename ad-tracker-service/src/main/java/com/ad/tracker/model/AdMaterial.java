package com.ad.tracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad_materials")
public class AdMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "advertiser_id")
    private Long advertiserId;
    
    private String title;
    
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
    
    private Integer status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public AdMaterial() {}
    
    public AdMaterial(Long id, Long advertiserId, String title, String type, String imageUrl, 
                     String videoUrl, String linkUrl, Integer width, Integer height, Integer duration, 
                     String category, String targetInterest, BigDecimal bidPrice, Integer status, 
                     LocalDateTime createdAt) {
        this.id = id;
        this.advertiserId = advertiserId;
        this.title = title;
        this.type = type;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.linkUrl = linkUrl;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.category = category;
        this.targetInterest = targetInterest;
        this.bidPrice = bidPrice;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAdvertiserId() {
        return advertiserId;
    }
    
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public String getLinkUrl() {
        return linkUrl;
    }
    
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTargetInterest() {
        return targetInterest;
    }
    
    public void setTargetInterest(String targetInterest) {
        this.targetInterest = targetInterest;
    }
    
    public BigDecimal getBidPrice() {
        return bidPrice;
    }
    
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}