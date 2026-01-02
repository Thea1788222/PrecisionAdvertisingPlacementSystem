package com.ad.management.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_fingerprint")
    private String userFingerprint;

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
}