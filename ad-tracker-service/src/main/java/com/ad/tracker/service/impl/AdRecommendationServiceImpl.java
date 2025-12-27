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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

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
     * 根据用户行为分决定推荐策略：
     * - 行为分 >= 50: 按兴趣权重分配80%，随机20%
     * - 行为分 < 50: 权重最高30% + 权重最低20% + 随机50%
     * - 新用户: 随机推荐
     */
    public List<AdMaterial> getRecommendedAds(String userFingerprint, String website, 
                                            List<String> positions, String category, String type, int count) {

        List<AdMaterial> result = new ArrayList<>(); // 结果列表
        Set<Long> usedAdIds = new HashSet<>(); // 用于去重
        
        // 如果请求的数量为0或负数，返回空列表
        if (count <= 0) {
            return result;
        }
        
        // 获取用户画像
        UserProfile userProfile = userProfileService.getUserProfileByFingerprint(userFingerprint);
        
        if (userProfile != null && userProfile.getBehaviorScore() != null) {
            if (userProfile.getBehaviorScore() >= 50) {
                // 高行为分用户：按兴趣权重分配80%，随机20%
                result = getRecommendationsForHighBehaviorScore(userFingerprint, userProfile, type, count, usedAdIds);
            } else {
                // 低行为分用户：权重最高30% + 权重最低20% + 随机50%
                result = getRecommendationsForLowBehaviorScore(userFingerprint, userProfile, type, count, usedAdIds);
            }
        } else {
            // 新用户：随机推荐
            result = getRandomRecommendations(count, usedAdIds, type);
        }
        
        return result;
    }
    
    /**
     * 高行为分用户的推荐策略：按兴趣权重分配80%，随机20%
     */
    private List<AdMaterial> getRecommendationsForHighBehaviorScore(String userFingerprint, UserProfile userProfile, 
                                                                 String type, int count, Set<Long> usedAdIds) {
        List<AdMaterial> result = new ArrayList<>();

        // 计算按权重推荐的数量（80%）
        int weightBasedCount = (int) (count * 0.8);
        
        // 按兴趣权重推荐（精准投放）
        List<AdMaterial> weightBasedAds = getPrecisionRecommendations(userFingerprint, userProfile, type, weightBasedCount, usedAdIds);
        result.addAll(weightBasedAds);
        
        // 剩余的20%补充随机广告
        int remainingCount = count - result.size();
        if (remainingCount > 0) {
            List<AdMaterial> randomAds = getRandomRecommendations(remainingCount, usedAdIds, type);
            result.addAll(randomAds);
        }
        
        return result;
    }
    
    /**
     * 低行为分用户的推荐策略：权重最高30% + 权重最低20% + 随机50%
     */
    private List<AdMaterial> getRecommendationsForLowBehaviorScore(String userFingerprint, UserProfile userProfile, 
                                                                 String type, int count, Set<Long> usedAdIds) {
        List<AdMaterial> result = new ArrayList<>();
        
        // 计算各部分推荐数量
        int topWeightCount = (int) (count * 0.3);      // 权重最高30%
        int bottomWeightCount = (int) (count * 0.2);   // 权重最低20%
        
        // 获取用户兴趣权重
        Map<String, Double> interests = userProfileService.parseInterests(userProfile);
        
        // 获取权重最高的类别广告（兴趣探索）
        List<AdMaterial> topWeightAds = getExplorationRecommendations(interests, type, topWeightCount, usedAdIds);
        result.addAll(topWeightAds);
        
        // 获取权重最低的类别广告（兴趣探索）
        int remainingCount = count - result.size();
        int currentBottomCount = Math.min(bottomWeightCount, remainingCount);
        List<AdMaterial> bottomWeightAds = getExplorationRecommendationsForLowest(interests, type, currentBottomCount, usedAdIds);
        result.addAll(bottomWeightAds);
        
        // 剩余的50%补充随机广告
        remainingCount = count - result.size();
        if (remainingCount > 0) {
            List<AdMaterial> randomAds = getRandomRecommendations(remainingCount, usedAdIds, type);
            result.addAll(randomAds);
        }
        
        return result;
    }
    
    /**
     * 精准投放推荐（高行为分用户）
     */
    private List<AdMaterial> getPrecisionRecommendations(String userFingerprint, UserProfile userProfile, 
                                                        String type, int count, Set<Long> usedAdIds) {
        List<AdMaterial> result = new ArrayList<>();
        
        // 获取用户兴趣权重
        Map<String, Double> interests = userProfileService.parseInterests(userProfile);
        
        if (interests.isEmpty()) {
            // 如果没有兴趣权重，使用随机推荐
            return getRandomRecommendations(count, usedAdIds, type);
        }
        
        // 获取所有相关广告
        List<AdMaterial> allRelevantAds = new ArrayList<>();
        for (String category : interests.keySet()) {
            List<AdMaterial> categoryAds;
            if (type != null && !type.isEmpty()) {
                // 按指定类型和分类获取启用的广告
                categoryAds = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(category, type, 1);
            } else {
                categoryAds = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(category, 1);
            }
            allRelevantAds.addAll(categoryAds);
        }
        
        // 获取用户行为数据用于计算兴趣权重
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserFingerprint(userFingerprint);
        
        // 计算每个广告的综合得分并排序
        List<AdMaterial> sortedMaterials = allRelevantAds.stream()
            .filter(ad -> !usedAdIds.contains(ad.getId())) // 过滤已使用的广告
            .sorted((a, b) -> {
                double scoreA = calculatePrecisionScore(a, userProfile, behaviors);
                double scoreB = calculatePrecisionScore(b, userProfile, behaviors);
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .toList();
        
        // 返回指定数量的广告
        List<AdMaterial> selectedAds = sortedMaterials.size() > count ? 
            sortedMaterials.subList(0, count) : sortedMaterials;
        
        result.addAll(selectedAds);
        
        // 更新已使用ID集合
        selectedAds.forEach(ad -> usedAdIds.add(ad.getId()));
        
        return result;
    }
    
    /**
     * 兴趣探索推荐（高权重类别）
     */
    private List<AdMaterial> getExplorationRecommendations(Map<String, Double> interests, String type, int count, Set<Long> usedAdIds) {
        if (interests.isEmpty()) {
            return getRandomRecommendations(count, usedAdIds, type);
        }
        
        // 获取权重最高的类别
        String topCategory = interests.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("electronics");
        
        List<AdMaterial> categoryAds;
        if (type != null && !type.isEmpty()) {
            categoryAds = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(topCategory, type, 1);
        } else {
            categoryAds = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(topCategory, 1);
        }
        
        // 计算类别匹配度并排序
        List<AdMaterial> sortedMaterials = categoryAds.stream()
            .filter(ad -> !usedAdIds.contains(ad.getId())) // 过滤已使用的广告
            .sorted((a, b) -> {
                double matchA = a.getBidPrice().doubleValue() * (1 + 0.5); // 类别匹配
                double matchB = b.getBidPrice().doubleValue() * (1 + 0.5); // 类别匹配
                
                return Double.compare(matchB, matchA); // 降序排列
            })
            .limit(count)
            .collect(Collectors.toList());
        
        // 更新已使用ID集合
        sortedMaterials.forEach(ad -> usedAdIds.add(ad.getId()));
        
        return sortedMaterials;
    }
    
    /**
     * 兴趣探索推荐（低权重类别）
     */
    private List<AdMaterial> getExplorationRecommendationsForLowest(Map<String, Double> interests, String type, int count, Set<Long> usedAdIds) {
        if (interests.isEmpty()) {
            return getRandomRecommendations(count, usedAdIds, type);
        }
        
        // 获取权重最低的类别
        String bottomCategory = interests.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("electronics");
        
        List<AdMaterial> categoryAds;
        if (type != null && !type.isEmpty()) {
            categoryAds = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(bottomCategory, type, 1);
        } else {
            categoryAds = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(bottomCategory, 1);
        }
        
        // 计算类别匹配度并排序
        List<AdMaterial> sortedMaterials = categoryAds.stream()
            .filter(ad -> !usedAdIds.contains(ad.getId())) // 过滤已使用的广告
            .sorted((a, b) -> {
                double matchA = a.getBidPrice().doubleValue() * (1 + 0.2); // 类别匹配度较低
                double matchB = b.getBidPrice().doubleValue() * (1 + 0.2); // 类别匹配度较低
                
                return Double.compare(matchB, matchA); // 降序排列
            })
            .limit(count)
            .collect(Collectors.toList());
        
        // 更新已使用ID集合
        sortedMaterials.forEach(ad -> usedAdIds.add(ad.getId()));
        
        return sortedMaterials;
    }
    
    /**
     * 随机推荐
     */
    private List<AdMaterial> getRandomRecommendations(int count, Set<Long> usedAdIds) {
        return getRandomRecommendations(count, usedAdIds, null);
    }
    
    /**
     * 随机推荐（支持类型筛选）
     */
    private List<AdMaterial> getRandomRecommendations(int count, Set<Long> usedAdIds, String type) {
        List<AdMaterial> result = new ArrayList<>();
        
        List<AdMaterial> availableAds;
        if (type != null && !type.isEmpty()) {
            // 按指定类型获取启用的广告，排除已使用的广告
            availableAds = adMaterialRepository.findByStatusAndType(1, type).stream()
                .filter(ad -> !usedAdIds.contains(ad.getId()))
                .collect(Collectors.toList());
        } else {
            // 获取所有启用的广告，排除已使用的广告
            List<AdMaterial> allAds = adMaterialRepository.findByStatus(1);
            availableAds = allAds.stream()
                .filter(ad -> !usedAdIds.contains(ad.getId()))
                .collect(Collectors.toList());
        }
        
        // 随机选择指定数量的广告
        Random random = new Random();
        if (availableAds.size() <= count) {
            result.addAll(availableAds);
            availableAds.forEach(ad -> usedAdIds.add(ad.getId()));
        } else {
            for (int i = 0; i < count && !availableAds.isEmpty(); i++) {
                int randomIndex = random.nextInt(availableAds.size());
                AdMaterial selectedAd = availableAds.get(randomIndex);
                result.add(selectedAd);
                usedAdIds.add(selectedAd.getId());
                availableAds.remove(randomIndex); // 避免重复选择
            }
        }
        
        return result;
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