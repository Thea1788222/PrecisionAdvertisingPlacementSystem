package com.ad.tracker.service.impl;

import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.UserBehaviorRepository;
import com.ad.tracker.repository.UserProfileRepository;
import com.ad.tracker.service.UserBehaviorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(UserBehaviorServiceImpl.class);
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_BEHAVIOR_QUEUE = "user_behavior_queue";
    private static final int BATCH_SIZE = 100;  // 批量处理数量
    private static final double VIEW_BASE_SCORE = 1;    // 浏览固定加分
    private static final double VIEW_DURATION_FACTOR = 0.5; // 每10秒加0.5分
    private static final double VIEW_DURATION_INTERVAL = 10; // 10秒为一个计分间隔
    private static final double VIEW_MAX_DURATION_SCORE = 5; // 浏览时长加分上限
    private static final double CLICK_SCORE = 1; // 点击固定加分
    private static final double TIME_DECAY_FACTOR = 0.1; // 时间衰减因子

    /**
     * 保存用户行为数据到Redis队列，等待批量处理
     */
    @Override
    public UserBehavior saveUserBehavior(UserBehavior userBehavior) {
        // 设置创建时间
        userBehavior.setCreatedAt(LocalDateTime.now());
        
        logger.info("准备将用户行为数据推送到Redis队列: 用户指纹={}, 行为类型={}, 分类={}, ID={}", 
                   userBehavior.getUserFingerprint(), userBehavior.getActionType(), userBehavior.getCategory(), userBehavior.getId());
        
        // 保存到Redis队列
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        long result = listOps.rightPush(USER_BEHAVIOR_QUEUE, userBehavior);
        
        logger.info("用户行为数据已推送到Redis队列: 队列长度变化结果={}", result);
        
        // 保存到数据库
        UserBehavior savedBehavior = userBehaviorRepository.save(userBehavior);
        logger.info("用户行为数据已保存到数据库: ID={}", savedBehavior.getId());
        
        return savedBehavior;
    }

    /**
     * 定时批量处理用户行为数据，更新用户画像
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
//    @Scheduled(cron = "0 */5 * * * ?") // 每五分钟执行一次
    @Transactional
    public void batchUpdateUserProfiles() {
        logger.info("开始执行批量更新用户画像任务");
        
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        long queueSize = listOps.size(USER_BEHAVIOR_QUEUE);
        
        logger.info("检查Redis队列中的用户行为数据，当前队列大小: {}", queueSize);
        
        if (queueSize == 0) {
            logger.info("Redis队列为空，结束批量更新任务");
            return;
        }
        
        // 批量取出行为数据
        int batchSize = Math.min(BATCH_SIZE, Math.toIntExact(queueSize));
        List<UserBehavior> batchBehaviors = new ArrayList<>();
        
        logger.info("准备从Redis队列取出 {} 个用户行为数据", batchSize);
        
        for (int i = 0; i < batchSize; i++) {
            Object behaviorObj = listOps.leftPop(USER_BEHAVIOR_QUEUE);
            logger.debug("从Redis队列取出对象类型: {}", behaviorObj != null ? behaviorObj.getClass().getName() : "null");
            logger.debug("从Redis队列取出对象内容: {}", behaviorObj);

            if (behaviorObj instanceof UserBehavior) {
                UserBehavior behavior = (UserBehavior) behaviorObj;
                logger.debug("成功转换为UserBehavior: 用户指纹={}, 行为类型={}, 分类={}",
                           behavior.getUserFingerprint(), behavior.getActionType(), behavior.getCategory());

                batchBehaviors.add(behavior);
            } else {
                logger.warn("对象无法转换为UserBehavior类型，实际类型: {}", 
                           behaviorObj != null ? behaviorObj.getClass().getName() : "null");
            }
        }
        
        logger.info("成功从Redis队列取出 {} 个UserBehavior对象", batchBehaviors.size());
        
        if (batchBehaviors.isEmpty()) {
            logger.warn("从Redis队列取出的行为数据为空，结束批量更新任务");
            return;
        }
        
        // 按用户指纹分组
        Map<String, List<UserBehavior>> behaviorsByFingerprint = 
            batchBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehavior::getUserFingerprint));
        
        logger.info("按用户指纹分组完成，共涉及 {} 个不同用户", behaviorsByFingerprint.size());
        
        // 为每个用户更新画像
        for (Map.Entry<String, List<UserBehavior>> entry : behaviorsByFingerprint.entrySet()) {
            String userFingerprint = entry.getKey();
            List<UserBehavior> userBehaviors = entry.getValue();
            
            logger.info("正在更新用户画像: 用户指纹={}, 行为数量={}", userFingerprint, userBehaviors.size());
            
            updateUserProfile(userFingerprint, userBehaviors);
        }
        
        logger.info("批量更新用户画像任务完成");
    }

    /**
     * 更新用户画像
     */
    private void updateUserProfile(String userFingerprint, List<UserBehavior> newBehaviors) {
        logger.info("开始更新用户画像: 用户指纹={}, 新行为数量={}", userFingerprint, newBehaviors.size());
        
        // 查找现有用户画像
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUserFingerprint(userFingerprint);
        UserProfile userProfile = userProfileOpt.orElse(new UserProfile());
        
        // 如果是新用户画像，初始化
        if (userProfile.getId() == null) {
            logger.info("创建新用户画像: 用户指纹={}", userFingerprint);
            userProfile.setUserFingerprint(userFingerprint);
            userProfile.setInterests("{}"); // 初始化为空JSON
            userProfile.setCategories("{}"); // 初始化为空JSON
            userProfile.setBehaviorScore(0); // 初始分数为0
        }
        
        logger.debug("用户画像当前行为分数: {}", userProfile.getBehaviorScore());
        
        // 解析现有兴趣和类别权重
        Map<String, Double> existingInterests = parseInterests(userProfile.getInterests());
        Map<String, Double> existingCategories = parseCategories(userProfile.getCategories());
        
        logger.debug("现有兴趣权重: {}", existingInterests);
        
        // 计算新行为的分数和兴趣权重
        Map<String, Double> newCategoryScores = calculateCategoryScores(userProfile, newBehaviors);
        
        logger.debug("新行为类别分数: {}", newCategoryScores);
        
        // 将新行为分数与现有兴趣权重进行合并
        Map<String, Double> mergedInterests = mergeCategoryScores(existingInterests, newCategoryScores);
        Map<String, Double> mergedCategories = mergeCategoryScores(existingCategories, newCategoryScores);
        
        logger.debug("合并后兴趣权重: {}", mergedInterests);
        
        // 更新用户画像
        // 计算新的行为分数并取整
        double newScore = calculateTotalScore(newBehaviors);
        int totalScore = (int) (userProfile.getBehaviorScore() + newScore);
        
        logger.debug("新增行为分数: {}, 总分数: {}", newScore, totalScore);
        
        userProfile.setBehaviorScore(totalScore);
        // 更新兴趣和类别权重
        String interestsJson = formatInterests(mergedInterests);
        String categoriesJson = formatCategories(mergedCategories);
        
        userProfile.setInterests(interestsJson);
        userProfile.setCategories(categoriesJson);
        userProfile.setLastActive(LocalDateTime.now());
        
        if (userProfile.getCreatedAt() == null) {
            userProfile.setCreatedAt(LocalDateTime.now());
        }
        
        logger.debug("准备保存用户画像: 用户指纹={}, 行为分数={}, 兴趣={}", 
                    userProfile.getUserFingerprint(), userProfile.getBehaviorScore(), userProfile.getInterests());
        
        // 保存用户画像
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        
        logger.info("用户画像已保存: ID={}, 用户指纹={}, 行为分数={}", 
                   savedProfile.getId(), savedProfile.getUserFingerprint(), savedProfile.getBehaviorScore());
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
    
    /**
     * 解析兴趣JSON字符串为Map
     */
    private Map<String, Double> parseInterests(String interestsJson) {
        if (interestsJson == null || interestsJson.isEmpty() || interestsJson.equals("{}")) {
            return new HashMap<>();
        }
        
        Map<String, Double> interests = new HashMap<>();
        
        // 简单解析JSON格式: {"category1":score1,"category2":score2}
        try {
            String content = interestsJson.replaceAll("^[{]", "").replaceAll("[}]$", "");
            if (!content.isEmpty()) {
                String[] pairs = content.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].replaceAll("^\"|\"$", ""); // 去除引号
                        Double value = Double.parseDouble(keyValue[1].trim());
                        interests.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("解析兴趣JSON失败: {}", interestsJson, e);
        }
        
        return interests;
    }
    
    /**
     * 解析类别JSON字符串为Map
     */
    private Map<String, Double> parseCategories(String categoriesJson) {
        if (categoriesJson == null || categoriesJson.isEmpty() || categoriesJson.equals("{}")) {
            return new HashMap<>();
        }
        
        Map<String, Double> categories = new HashMap<>();
        
        // 简单解析JSON格式: {"category1":1,"category2":1}
        try {
            String content = categoriesJson.replaceAll("^[{]", "").replaceAll("[}]$", "");
            if (!content.isEmpty()) {
                String[] pairs = content.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].replaceAll("^\"|\"$", ""); // 去除引号
                        Double value = Double.parseDouble(keyValue[1].trim());
                        categories.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("解析类别JSON失败: {}", categoriesJson, e);
        }
        
        return categories;
    }
    
    /**
     * 合并新旧类别分数
     */
    private Map<String, Double> mergeCategoryScores(Map<String, Double> existingScores, Map<String, Double> newScores) {
        Map<String, Double> mergedScores = new HashMap<>(existingScores);
        
        for (Map.Entry<String, Double> newEntry : newScores.entrySet()) {
            String category = newEntry.getKey();
            Double newScore = newEntry.getValue();
            
            // 将新分数与现有分数相加
            mergedScores.merge(category, newScore, Double::sum);
        }
        
        return mergedScores;
    }
}