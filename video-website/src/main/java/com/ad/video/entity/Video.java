package com.ad.video.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Integer duration; // 秒

    @Column(name = "play_url")
    private String playUrl;

    @Column(name = "cover_url")
    private String coverUrl;

    private String format;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private VideoCategory category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum VideoCategory {
        ELECTRONICS("electronics"),
        FASHION("fashion"),
        SPORTS("sports"),
        HOME("home"),
        FOOD("food"),
        TRAVEL("travel"),
        EDUCATION("education"),
        FINANCE("finance"),
        HEALTH("health"),
        BEAUTY("beauty");
        
        private final String value;
        
        VideoCategory(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
}
