package com.ad.tracker.service.impl;

import com.ad.tracker.model.AdMaterial;
import com.ad.tracker.model.UserProfile;
import com.ad.tracker.repository.AdMaterialRepository;
import com.ad.tracker.repository.UserBehaviorRepository;
import com.ad.tracker.model.UserBehavior;
import com.ad.tracker.service.AdRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class AdRecommendationServiceImpl implements AdRecommendationService {
    
    @Autowired
    private AdMaterialRepository adMaterialRepository;
    
    @Autowired
    private UserProfileServiceImpl userProfileService;
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    /**
     * 获取推荐广告
     */
    public List<AdMaterial> getRecommendedAds(String userFingerprint, String website, 
                                            List<String> positions, String category, int count) {
        // 获取用户画像
        UserProfile userProfile = userProfileService.getUserProfileByFingerprint(userFingerprint);
        
        // 根据用户画像阶段选择不同的推荐策略
        List<AdMaterial> materials;
        if (userProfile == null) {
            // 阶段一：冷启动（新用户）
            materials = getColdStartRecommendations(category);
        } else if (userProfile.getBehaviorScore() != null && userProfile.getBehaviorScore() < 50) {
            // 阶段二：兴趣探索（有少量行为数据）
            materials = getExplorationRecommendations(userProfile, category);
        } else {
            // 阶段三：精准投放（有充分数据）
            materials = getPrecisionRecommendations(userFingerprint, userProfile, category);
        }
        
        // 如果指定了数量，截取相应数量的广告
        if (count > 0 && materials.size() > count) {
            materials = materials.subList(0, count);
        }
        
        return materials;
    }
    
    /**
     * 阶段一：冷启动（新用户）
     * 广告分数 = 出价 × 随机因子
     */
    private List<AdMaterial> getColdStartRecommendations(String category) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                category, 1); // 1表示启用状态
        } else {
            // 如果没有指定类别，则返回所有启用状态的广告作为兜底
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                "electronics", 1); // 默认使用电子产品类别
        }
        
        // 添加随机因子
        Random random = new Random();
        return materials.stream()
            .sorted((a, b) -> {
                double scoreA = a.getBidPrice().doubleValue() * random.nextDouble();
                double scoreB = b.getBidPrice().doubleValue() * random.nextDouble();
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 阶段二：兴趣探索（有少量行为数据）
     * 广告分数 = 出价 × (1 + 类别匹配度 × 0.5)
     */
    private List<AdMaterial> getExplorationRecommendations(UserProfile userProfile, String category) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                category, 1);
        } else if (userProfile.getCategories() != null && !userProfile.getCategories().isEmpty()) {
            // 根据用户画像中的类别推荐
            String[] categories = userProfile.getCategories().split(",");
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                categories[0], 1);
        } else {
            // 兜底方案
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                "electronics", 1);
        }
        
        // 计算类别匹配度并排序
        String userCategory = userProfile.getCategories() != null ? 
            userProfile.getCategories().split(",")[0] : "electronics";
            
        return materials.stream()
            .sorted((a, b) -> {
                double matchA = a.getCategory().equals(userCategory) ? 1.0 : 0.0;
                double matchB = b.getCategory().equals(userCategory) ? 1.0 : 0.0;
                
                double scoreA = a.getBidPrice().doubleValue() * (1 + matchA * 0.5);
                double scoreB = b.getBidPrice().doubleValue() * (1 + matchB * 0.5);
                
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 阶段三：精准投放（有充分数据）
     * 广告分数 = 出价 × (1 + 兴趣权重 + 时间衰减)
     * 兴趣权重 = Σ(行为权重 × 类别相关度)
     * 行为权重：浏览(0.1) < 搜索(0.3) < 点击(0.5) < 购买(1.0)
     * 时间衰减：recent_score = score × e^(-λt)，λ=0.1
     */
    private List<AdMaterial> getPrecisionRecommendations(String userFingerprint, UserProfile userProfile, String category) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                category, 1);
        } else if (userProfile.getCategories() != null && !userProfile.getCategories().isEmpty()) {
            // 根据用户画像中的类别推荐
            String[] categories = userProfile.getCategories().split(",");
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                categories[0], 1);
        } else {
            // 兜底方案
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(
                "electronics", 1);
        }
        
        // 获取用户行为数据用于计算兴趣权重
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserFingerprint(userFingerprint);
        
        // 计算每个广告的综合得分
        return materials.stream()
            .sorted((a, b) -> {
                double scoreA = calculatePrecisionScore(a, userProfile, behaviors);
                double scoreB = calculatePrecisionScore(b, userProfile, behaviors);
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 计算精准投放阶段的广告得分
     */
    private double calculatePrecisionScore(AdMaterial ad, UserProfile userProfile, List<UserBehavior> behaviors) {
        // 基础出价
        double bidPrice = ad.getBidPrice().doubleValue();
        
        // 计算兴趣权重
        double interestWeight = calculateInterestWeight(ad, behaviors);
        
        // 计算时间衰减因子 (λ=0.1)
        double timeDecay = calculateTimeDecay(userProfile);
        
        // 综合得分 = 出价 × (1 + 兴趣权重 + 时间衰减)
        return bidPrice * (1 + interestWeight + timeDecay);
    }
    
    /**
     * 计算兴趣权重
     * 兴趣权重 = Σ(行为权重 × 类别相关度)
     * 行为权重：浏览(0.1) < 搜索(0.3) < 点击(0.5) < 购买(1.0)
     */
    private double calculateInterestWeight(AdMaterial ad, List<UserBehavior> behaviors) {
        double weight = 0.0;
        
        for (UserBehavior behavior : behaviors) {
            if (behavior.getCategory() == null || !behavior.getCategory().equals(ad.getCategory())) {
                continue;
            }
            
            // 根据行为类型确定权重
            double behaviorWeight = switch (behavior.getActionType()) {
                case "view" -> 0.1;
                case "search" -> 0.3;
                case "click" -> 0.5;
                case "purchase" -> // 假设购买行为
                        1.0;
                default -> 0.1; // 默认权重
            };

            weight += behaviorWeight; // 类别完全匹配时相关度为1.0
        }
        
        return weight;
    }
    
    /**
     * 计算时间衰减因子
     * recent_score = score × e^(-λt)，λ=0.1
     */
    private double calculateTimeDecay(UserProfile userProfile) {
        if (userProfile.getLastActive() == null) {
            return 0.0;
        }
        
        long hoursSinceLastActive = ChronoUnit.HOURS.between(userProfile.getLastActive(), LocalDateTime.now());
        double lambda = 0.1; // 衰减系数
        
        // 时间衰减函数，越近的行为权重越高
        return Math.exp(-lambda * hoursSinceLastActive);
    }
}