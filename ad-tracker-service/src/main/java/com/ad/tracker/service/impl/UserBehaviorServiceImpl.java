package com.ad.tracker.service.impl;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserBehaviorRepository;
import com.ad.tracker.repository.UserProfileRepository;
import com.ad.tracker.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_BEHAVIOR_QUEUE = "user_behavior_queue";
    private static final int BATCH_SIZE = 100;  // 批量处理数量
    private static final double VIEW_BASE_SCORE = 5;    // 浏览固定加分
    private static final double VIEW_DURATION_FACTOR = 0.5; // 每10秒加0.5分
    private static final double VIEW_DURATION_INTERVAL = 10; // 10秒为一个计分间隔
    private static final double VIEW_MAX_DURATION_SCORE = 10; // 浏览时长加分上限
    private static final double CLICK_SCORE = 5; // 点击固定加分
    private static final double TIME_DECAY_FACTOR = 0.1; // 时间衰减因子

    /**
     * 保存用户行为数据到Redis队列，等待批量处理
     */
    @Override
    public UserBehavior saveUserBehavior(UserBehavior userBehavior) {
        // 设置创建时间
        userBehavior.setCreatedAt(LocalDateTime.now());
        
        // 保存到Redis队列
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        listOps.rightPush(USER_BEHAVIOR_QUEUE, userBehavior);
        
        // 保存到数据库
        return userBehaviorRepository.save(userBehavior);
    }

    /**
     * 定时批量处理用户行为数据，更新用户画像
     */
    @Scheduled(fixedRate = 30000) // 每30秒执行一次
    @Transactional
    public void batchUpdateUserProfiles() {
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        long queueSize = listOps.size(USER_BEHAVIOR_QUEUE);
        
        if (queueSize == 0) {
            return;
        }
        
        // 批量取出行为数据
        int batchSize = Math.min(BATCH_SIZE, Math.toIntExact(queueSize));
        List<UserBehavior> batchBehaviors = new ArrayList<>();
        
        for (int i = 0; i < batchSize; i++) {
            Object behaviorObj = listOps.leftPop(USER_BEHAVIOR_QUEUE);
            if (behaviorObj instanceof UserBehavior) {
                batchBehaviors.add((UserBehavior) behaviorObj);
            }
        }
        
        if (batchBehaviors.isEmpty()) {
            return;
        }
        
        // 按用户指纹分组
        Map<String, List<UserBehavior>> behaviorsByFingerprint = 
            batchBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehavior::getUserFingerprint));
        
        // 为每个用户更新画像
        for (Map.Entry<String, List<UserBehavior>> entry : behaviorsByFingerprint.entrySet()) {
            String userFingerprint = entry.getKey();
            List<UserBehavior> userBehaviors = entry.getValue();
            
            updateUserProfile(userFingerprint, userBehaviors);
        }
    }

    /**
     * 更新用户画像
     */
    private void updateUserProfile(String userFingerprint, List<UserBehavior> newBehaviors) {
        // 查找现有用户画像
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUserFingerprint(userFingerprint);
        UserProfile userProfile = userProfileOpt.orElse(new UserProfile());
        
        // 如果是新用户画像，初始化
        if (userProfile.getId() == null) {
            userProfile.setUserFingerprint(userFingerprint);
            userProfile.setInterests("{}"); // 初始化为空JSON
            userProfile.setCategories("{}"); // 初始化为空JSON
            userProfile.setBehaviorScore(0); // 初始分数为0
        }
        
        // 计算新的行为分数和兴趣权重
        Map<String, Double> categoryScores = calculateCategoryScores(userProfile, newBehaviors);
        
        // 更新用户画像
        // 计算新的行为分数并取整
        userProfile.setBehaviorScore((int) (userProfile.getBehaviorScore() + calculateTotalScore(newBehaviors)));
        // 更新兴趣和类别权重
        userProfile.setInterests(formatInterests(categoryScores));
        userProfile.setCategories(formatCategories(categoryScores));
        userProfile.setLastActive(LocalDateTime.now());
        
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }
        
        // 保存用户画像
        userProfileRepository.save(userProfile);
    }

    /**
     * 计算各类别的兴趣权重
     */
    private Map<String, Double> calculateCategoryScores(UserProfile userProfile, List<UserBehavior> newBehaviors) {
        Map<String, Double> categoryScores = new HashMap<>();
        
        // 计算新行为的权重
        for (UserBehavior behavior : newBehaviors) {
            if (behavior.getCategory() == null) continue;

            // 计算行为得分
            double behaviorScore = calculateBehaviorScore(behavior);
            // 计算时间衰减因子
            double timeDecay = calculateTimeDecay(behavior.getCreatedAt());
            // 计算加权得分
            double weightedScore = behaviorScore * timeDecay;

            // 合并类别得分
            categoryScores.merge(behavior.getCategory(), weightedScore, Double::sum);
        }
        
        return categoryScores;
    }

    /**
     * 计算单个行为的得分
     */
    private double calculateBehaviorScore(UserBehavior behavior) {
        return switch (behavior.getActionType()) {
            case "view" -> {
                // 浏览行为：基础分 + 时长加权分
                double durationScore = Math.min(
                        VIEW_MAX_DURATION_SCORE,
                        (behavior.getDuration() / VIEW_DURATION_INTERVAL) * VIEW_DURATION_FACTOR
                );
                yield VIEW_BASE_SCORE + durationScore;
            }
            case "click" ->
                // 点击行为：固定加分
                    CLICK_SCORE;
            default -> 0.0;
        };
    }

    /**
     * 计算时间衰减因子
     */
    private double calculateTimeDecay(LocalDateTime behaviorTime) {
        if (behaviorTime == null) {
            return 1.0; // 如果没有时间，默认不衰减
        }
        
        long hoursDiff = ChronoUnit.HOURS.between(behaviorTime, LocalDateTime.now());
        // 使用指数衰减：e^(-λt)，λ为衰减因子
        return Math.exp(-TIME_DECAY_FACTOR * hoursDiff);
    }

    /**
     * 计算总行为分数
     */
    private double calculateTotalScore(List<UserBehavior> behaviors) {
        return behaviors.stream()
            .mapToDouble(this::calculateBehaviorScore)
            .sum();
    }

    /**
     * 格式化兴趣为JSON字符串（按权重降序排列）
     */
    private String formatInterests(Map<String, Double> categoryScores) {
        // 按权重降序排序
        List<Map.Entry<String, Double>> sortedEntries = categoryScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5) // 只保留前5个兴趣
            .toList();
        
        // 构建JSON字符串
        StringBuilder jsonBuilder = new StringBuilder("{");
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Double> entry = sortedEntries.get(i);
            jsonBuilder.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            if (i < sortedEntries.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("}");
        
        return jsonBuilder.toString();
    }

    /**
     * 格式化类别为JSON字符串
     */
    private String formatCategories(Map<String, Double> categoryScores) {
        // 类别可以简单地包含所有出现的类别
        StringBuilder jsonBuilder = new StringBuilder("{");
        List<String> categories = new ArrayList<>(categoryScores.keySet());
        
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            jsonBuilder.append("\"").append(category).append("\":1");
            if (i < categories.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("}");
        
        return jsonBuilder.toString();
    }
}