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
     * 实现多阶段组合推荐，确保返回指定数量的广告
     * 优先返回指定类型的广告，如果数量不足则用其他类型填充
     */
    public List<AdMaterial> getRecommendedAds(String userFingerprint, String website, 
                                            List<String> positions, String category, String type, int count) {

        List<AdMaterial> result = new ArrayList<>();
        Set<Long> usedAdIds = new HashSet<>(); // 用于去重
        
        // 如果请求的数量为0或负数，返回空列表
        if (count <= 0) {
            return result;
        }
        
        // 获取用户画像
        UserProfile userProfile = userProfileService.getUserProfileByFingerprint(userFingerprint);
        
        // 如果指定了类型，优先获取指定类型的广告
        if (type != null && !type.isEmpty()) {
            // 阶段1：用户的行为分 >= 50的精准投放广告
            if (userProfile != null && userProfile.getBehaviorScore() != null && userProfile.getBehaviorScore() >= 50) {
                // 获取指定类型的精准投放广告
                List<AdMaterial> precisionAds = getPrecisionAdsByType(userFingerprint, userProfile, category, type, count);
                for (AdMaterial ad : precisionAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段2：用户行为分 < 50的兴趣探索广告
            if (result.size() < count && userProfile != null) {
                // 获取指定类型的兴趣探索广告
                List<AdMaterial> explorationAds = getExplorationAdsByType(userProfile, category, type, count - result.size());
                for (AdMaterial ad : explorationAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段3：用户行为分为0的冷启动广告
            // 获取指定类型的冷启动广告
            if (result.size() < count) {
                List<AdMaterial> coldStartAds = getColdStartAdsByType(category, type, count - result.size());
                for (AdMaterial ad : coldStartAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段4：如果指定类型的广告仍然不足，用其他类型填充
            if (result.size() < count) {
                int remainingCount = count - result.size();
                
                // 先尝试获取其他类型的高优先级广告
                if (userProfile != null && userProfile.getBehaviorScore() != null && userProfile.getBehaviorScore() >= 50) {
                    List<AdMaterial> otherTypePrecisionAds = getPrecisionAdsByOtherTypes(userFingerprint, userProfile, category, type, remainingCount);
                    for (AdMaterial ad : otherTypePrecisionAds) {
                        if (usedAdIds.add(ad.getId()) && result.size() < count) {
                            result.add(ad);
                        }
                    }
                }
                
                remainingCount = count - result.size();
                if (result.size() < count && userProfile != null) {
                    List<AdMaterial> otherTypeExplorationAds = getExplorationAdsByOtherTypes(userProfile, category, type, remainingCount);
                    for (AdMaterial ad : otherTypeExplorationAds) {
                        if (usedAdIds.add(ad.getId()) && result.size() < count) {
                            result.add(ad);
                        }
                    }
                }
                
                remainingCount = count - result.size();
                if (result.size() < count) {
                    List<AdMaterial> otherTypeColdStartAds = getColdStartAdsByOtherTypes(category, type, remainingCount);
                    for (AdMaterial ad : otherTypeColdStartAds) {
                        if (usedAdIds.add(ad.getId()) && result.size() < count) {
                            result.add(ad);
                        }
                    }
                }
                
                // 阶段5：兜底策略 - 如果仍然不足，补充随机广告（包含所有类型）
                if (result.size() < count) {
                    List<AdMaterial> randomAds = getRandomAdsByOtherTypes(count - result.size(), usedAdIds, type);
                    result.addAll(randomAds);
                }
            }
        } else {
            // 如果没有指定类型
            // 阶段1：精准投放 - 获取最符合用户画像的广告
            if (userProfile != null && userProfile.getBehaviorScore() != null && userProfile.getBehaviorScore() >= 50) {
                List<AdMaterial> precisionAds = getPrecisionAds(userFingerprint, userProfile, category, count);
                for (AdMaterial ad : precisionAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段2：兴趣探索 - 如果数量不足，补充兴趣探索阶段的广告
            if (result.size() < count && userProfile != null) {
                List<AdMaterial> explorationAds = getExplorationAds(userProfile, category, count - result.size());
                for (AdMaterial ad : explorationAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段3：冷启动 - 如果数量仍然不足，补充冷启动阶段的广告
            if (result.size() < count) {
                List<AdMaterial> coldStartAds = getColdStartAds(category, count - result.size());
                for (AdMaterial ad : coldStartAds) {
                    if (usedAdIds.add(ad.getId()) && result.size() < count) {
                        result.add(ad);
                    }
                }
            }
            
            // 阶段4：兜底策略 - 如果仍然不足，补充随机广告
            if (result.size() < count) {
                List<AdMaterial> randomAds = getRandomAds(count - result.size(), usedAdIds);
                result.addAll(randomAds);
            }
        }
        
        return result;
    }
    
    /**
     * 获取精准投放阶段的广告
     */
    private List<AdMaterial> getPrecisionAds(String userFingerprint, UserProfile userProfile, String category, int count) {
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(category, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(topInterestCategory, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc("electronics", 1);
            }
        }
        
        // 获取用户行为数据用于计算兴趣权重
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserFingerprint(userFingerprint);
        
        // 计算每个广告的综合得分并排序
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = calculatePrecisionScore(a, userProfile, behaviors);
                double scoreB = calculatePrecisionScore(b, userProfile, behaviors);
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 获取兴趣探索阶段的广告
     */
    private List<AdMaterial> getExplorationAds(UserProfile userProfile, String category, int count) {
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(category, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(topInterestCategory, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc("electronics", 1);
            }
        }
        
        // 计算类别匹配度并排序
        String userCategory = userProfileService.getTopInterestCategory(userProfile);
        if (userCategory == null) {
            userCategory = "electronics";
        }

        String finalUserCategory = userCategory;
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double matchA = a.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                double matchB = b.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                
                double scoreA = a.getBidPrice().doubleValue() * (1 + matchA * 0.5);
                double scoreB = b.getBidPrice().doubleValue() * (1 + matchB * 0.5);
                
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 获取冷启动阶段的广告
     */
    private List<AdMaterial> getColdStartAds(String category, int count) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc(category, 1);
        } else {
            // 如果没有指定类别，则返回所有启用状态的广告作为兜底
            materials = adMaterialRepository.findByCategoryAndStatusOrderByBidPriceDesc("electronics", 1); // 默认使用电子产品类别
        }
        
        // 添加随机因子
        Random random = new Random();
        List<AdMaterial> shuffledMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = a.getBidPrice().doubleValue() * random.nextDouble();
                double scoreB = b.getBidPrice().doubleValue() * random.nextDouble();
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return shuffledMaterials.size() > count ? shuffledMaterials.subList(0, count) : shuffledMaterials;
    }
    
    /**
     * 获取随机广告作为兜底
     */
    private List<AdMaterial> getRandomAds(int count, Set<Long> excludeIds) {
        // 获取所有启用的广告，排除已使用的
        List<AdMaterial> allAds = adMaterialRepository.findByStatus(1);
        
        // 过滤掉已使用的广告
        List<AdMaterial> availableAds = allAds.stream()
            .filter(ad -> !excludeIds.contains(ad.getId()))
            .collect(Collectors.toList());
        
        // 随机打乱并返回指定数量
        if (availableAds.size() <= count) {
            return availableAds;
        }
        
        // 使用随机采样获取指定数量的广告
        Random random = new Random();
        List<AdMaterial> result = new ArrayList<>();
        for (int i = 0; i < count && i < availableAds.size(); i++) {
            int randomIndex = random.nextInt(availableAds.size() - i) + i;
            AdMaterial temp = availableAds.get(randomIndex);
            availableAds.set(randomIndex, availableAds.get(i));
            availableAds.set(i, temp);
            result.add(temp);
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
        double interestWeight = calculateInterestWeight(ad, behaviors, userProfile);
        
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
    private double calculateInterestWeight(AdMaterial ad, List<UserBehavior> behaviors, UserProfile userProfile) {
        double weight = 0.0;
        
        // 从用户画像中获取兴趣权重
        Map<String, Double> userInterests = userProfileService.parseInterests(userProfile);
        Double categoryWeight = userInterests.get(ad.getCategory());
        if (categoryWeight != null) {
            weight += categoryWeight;
        }
        
        // 同时考虑用户行为
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
    
    /**
     * 根据类型获取精准投放阶段的广告
     */
    private List<AdMaterial> getPrecisionAdsByType(String userFingerprint, UserProfile userProfile, String category, String type, int count) {
        
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            // 根据类别推荐
            materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(category, type, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(topInterestCategory, type, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc("electronics", type, 1);
            }
        }
        
        // 获取用户行为数据用于计算兴趣权重
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserFingerprint(userFingerprint);
         
        // 计算每个广告的综合得分并排序
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = calculatePrecisionScore(a, userProfile, behaviors);
                double scoreB = calculatePrecisionScore(b, userProfile, behaviors);
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 根据类型获取兴趣探索阶段的广告
     */
    private List<AdMaterial> getExplorationAdsByType(UserProfile userProfile, String category, String type, int count) {
        
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            // 根据类别推荐
            materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(category, type, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(topInterestCategory, type, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc("electronics", type, 1);
            }
        }
        
        // 计算类别匹配度并排序
        String userCategory = userProfileService.getTopInterestCategory(userProfile);
        if (userCategory == null) {
            userCategory = "electronics";
        }

        String finalUserCategory = userCategory;
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double matchA = a.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                double matchB = b.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                
                double scoreA = a.getBidPrice().doubleValue() * (1 + matchA * 0.5);
                double scoreB = b.getBidPrice().doubleValue() * (1 + matchB * 0.5);
                
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 根据类型获取冷启动阶段的广告
     */
    private List<AdMaterial> getColdStartAdsByType(String category, String type, int count) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            // 根据类别推荐
            materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc(category, type, 1);
        } else {
            // 如果没有指定类别，则返回所有启用状态的广告作为兜底
            materials = adMaterialRepository.findByCategoryAndTypeAndStatusOrderByBidPriceDesc("electronics", type, 1); // 默认使用电子产品类别
        }
        
        // 添加随机因子
        Random random = new Random();
        List<AdMaterial> shuffledMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = a.getBidPrice().doubleValue() * random.nextDouble();
                double scoreB = b.getBidPrice().doubleValue() * random.nextDouble();
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return shuffledMaterials.size() > count ? shuffledMaterials.subList(0, count) : shuffledMaterials;
    }
    
    /**
     * 获取其他类型的精准投放广告
     */
    private List<AdMaterial> getPrecisionAdsByOtherTypes(String userFingerprint, UserProfile userProfile, String category, String excludedType, int count) {
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            // 根据类别推荐
            materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(category, excludedType, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(topInterestCategory, excludedType, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc("electronics", excludedType, 1);
            }
        }
        
        // 获取用户行为数据用于计算兴趣权重
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserFingerprint(userFingerprint);
        
        // 计算每个广告的综合得分并排序
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = calculatePrecisionScore(a, userProfile, behaviors);
                double scoreB = calculatePrecisionScore(b, userProfile, behaviors);
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 获取其他类型的兴趣探索广告
     */
    private List<AdMaterial> getExplorationAdsByOtherTypes(UserProfile userProfile, String category, String excludedType, int count) {
        List<AdMaterial> materials;
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(category, excludedType, 1);
        } else {
            // 使用用户画像中的兴趣类别
            String topInterestCategory = userProfileService.getTopInterestCategory(userProfile);
            if (topInterestCategory != null) {
                materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(topInterestCategory, excludedType, 1);
            } else {
                // 兜底方案
                materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc("electronics", excludedType, 1);
            }
        }
        
        // 计算类别匹配度并排序
        String userCategory = userProfileService.getTopInterestCategory(userProfile);
        if (userCategory == null) {
            userCategory = "electronics";
        }

        String finalUserCategory = userCategory;
        List<AdMaterial> sortedMaterials = materials.stream()
            .sorted((a, b) -> {
                double matchA = a.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                double matchB = b.getCategory().equals(finalUserCategory) ? 1.0 : 0.0;
                
                double scoreA = a.getBidPrice().doubleValue() * (1 + matchA * 0.5);
                double scoreB = b.getBidPrice().doubleValue() * (1 + matchB * 0.5);
                
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return sortedMaterials.size() > count ? sortedMaterials.subList(0, count) : sortedMaterials;
    }
    
    /**
     * 获取其他类型的冷启动广告
     */
    private List<AdMaterial> getColdStartAdsByOtherTypes(String category, String excludedType, int count) {
        List<AdMaterial> materials = new ArrayList<>();
        
        if (category != null && !category.isEmpty()) {
            materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc(category, excludedType, 1);
        } else {
            // 如果没有指定类别，则返回所有启用状态的广告作为兜底
            materials = adMaterialRepository.findByCategoryAndStatusExcludingTypeOrderByBidPriceDesc("electronics", excludedType, 1); // 默认使用电子产品类别
        }
        
        // 添加随机因子
        Random random = new Random();
        List<AdMaterial> shuffledMaterials = materials.stream()
            .sorted((a, b) -> {
                double scoreA = a.getBidPrice().doubleValue() * random.nextDouble();
                double scoreB = b.getBidPrice().doubleValue() * random.nextDouble();
                return Double.compare(scoreB, scoreA); // 降序排列
            })
            .collect(Collectors.toList());
        
        // 返回指定数量的广告
        return shuffledMaterials.size() > count ? shuffledMaterials.subList(0, count) : shuffledMaterials;
    }
    
    /**
     * 获取其他类型的随机广告作为兜底
     */
    private List<AdMaterial> getRandomAdsByOtherTypes(int count, Set<Long> excludeIds, String excludedType) {
        // 获取所有启用的广告，排除已使用的和指定类型
        List<AdMaterial> allAds = adMaterialRepository.findByStatusExcludingType(1, excludedType);
        
        // 过滤掉已使用的广告
        List<AdMaterial> availableAds = allAds.stream()
            .filter(ad -> !excludeIds.contains(ad.getId()))
            .collect(Collectors.toList());
        
        // 随机打乱并返回指定数量
        if (availableAds.size() <= count) {
            return availableAds;
        }
        
        // 使用随机采样获取指定数量的广告
        Random random = new Random();
        List<AdMaterial> result = new ArrayList<>();
        for (int i = 0; i < count && i < availableAds.size(); i++) {
            int randomIndex = random.nextInt(availableAds.size() - i) + i;
            AdMaterial temp = availableAds.get(randomIndex);
            availableAds.set(randomIndex, availableAds.get(i));
            availableAds.set(i, temp);
            result.add(temp);
        }
        
        return result;
    }
}