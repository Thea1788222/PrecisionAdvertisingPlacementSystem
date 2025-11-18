package com.ad.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cookie_id")
    private String cookieId;
    
    @Column(name = "user_fingerprint")
    private String userFingerprint;
    
    private String interests;
    
    private String categories;
    
    @Column(name = "behavior_score")
    private Integer behaviorScore;
    
    @Column(name = "last_active")
    private LocalDateTime lastActive;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public UserProfile() {}
    
    public UserProfile(Long id, String cookieId, String userFingerprint, String interests, 
                      String categories, Integer behaviorScore, LocalDateTime lastActive, 
                      LocalDateTime createdAt) {
        this.id = id;
        this.cookieId = cookieId;
        this.userFingerprint = userFingerprint;
        this.interests = interests;
        this.categories = categories;
        this.behaviorScore = behaviorScore;
        this.lastActive = lastActive;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCookieId() {
        return cookieId;
    }
    
    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }
    
    public String getUserFingerprint() {
        return userFingerprint;
    }
    
    public void setUserFingerprint(String userFingerprint) {
        this.userFingerprint = userFingerprint;
    }
    
    public String getInterests() {
        return interests;
    }
    
    public void setInterests(String interests) {
        this.interests = interests;
    }
    
    public String getCategories() {
        return categories;
    }
    
    public void setCategories(String categories) {
        this.categories = categories;
    }
    
    public Integer getBehaviorScore() {
        return behaviorScore;
    }
    
    public void setBehaviorScore(Integer behaviorScore) {
        this.behaviorScore = behaviorScore;
    }
    
    public LocalDateTime getLastActive() {
        return lastActive;
    }
    
    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}