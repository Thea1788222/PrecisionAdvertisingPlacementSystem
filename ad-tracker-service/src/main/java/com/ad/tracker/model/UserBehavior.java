package com.ad.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
public class UserBehavior {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_fingerprint")
    private String userFingerprint;
    
    @Column(name = "cookie_id")
    private String cookieId;
    
    private String website;
    
    @Column(name = "action_type")
    private String actionType;
    
    @Column(name = "target_id")
    private String targetId;
    
    private String category;
    
    private String keywords;
    
    private Integer duration;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public UserBehavior() {}
    
    public UserBehavior(Long id, String userFingerprint, String cookieId, String website, 
                       String actionType, String targetId, String category, String keywords, 
                       Integer duration, String ipAddress, String userAgent, LocalDateTime createdAt) {
        this.id = id;
        this.userFingerprint = userFingerprint;
        this.cookieId = cookieId;
        this.website = website;
        this.actionType = actionType;
        this.targetId = targetId;
        this.category = category;
        this.keywords = keywords;
        this.duration = duration;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserFingerprint() {
        return userFingerprint;
    }
    
    public void setUserFingerprint(String userFingerprint) {
        this.userFingerprint = userFingerprint;
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
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}