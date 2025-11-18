package com.ad.tracker.service;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserBehaviorService {
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    
    public UserBehavior saveUserBehavior(UserBehavior userBehavior) {
        userBehavior.setCreatedAt(LocalDateTime.now());
        return userBehaviorRepository.save(userBehavior);
    }
}