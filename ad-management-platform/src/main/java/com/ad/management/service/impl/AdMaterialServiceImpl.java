package com.ad.management.service.impl;

import com.ad.management.model.AdMaterial;
import com.ad.management.repository.AdMaterialRepository;
import com.ad.management.service.AdMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdMaterialServiceImpl implements AdMaterialService {
    
    private final AdMaterialRepository adMaterialRepository;
    
    public AdMaterialServiceImpl(AdMaterialRepository adMaterialRepository) {
        this.adMaterialRepository = adMaterialRepository;
    }

    /**
     * 获取所有广告素材
     *
     * @param advertiserId 广告商ID
     * @param type         素材类型
     * @param category     素材分类
     * @param status       素材状态
     * @param keyword      搜索关键词
     * @param pageable     分页参数
     * @return 广告素材列表
     */
    @Override
    public Page<AdMaterial> getAllAdMaterials(Long advertiserId, String type, String category, Integer status, String keyword, Pageable pageable) {
        return adMaterialRepository.findByFilters(advertiserId, type, category, status, keyword, pageable);
    }

    /**
     * 创建广告素材
     *
     * @param adMaterial 广告素材对象
     * @return 创建的广告素材对象
     */
    @Override
    public AdMaterial createAdMaterial(AdMaterial adMaterial) {
        return adMaterialRepository.save(adMaterial);
    }

    /**
     * 获取广告素材ById
     *
     * @param id 广告素材ID
     * @return 获取的广告素材对象
     */
    @Override
    public Optional<AdMaterial> getAdMaterialById(Long id) {
        return adMaterialRepository.findById(id);
    }

    /**
     * 更新广告素材
     *
     * @param id                  广告素材ID
     * @param adMaterialDetails   广告素材详情
     * @return 更新的广告素材对象
     */
    @Override
    public AdMaterial updateAdMaterial(Long id, AdMaterial adMaterialDetails) {
        AdMaterial adMaterial = adMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告素材未找到"));
        
        adMaterial.setTitle(adMaterialDetails.getTitle());
        adMaterial.setImageUrl(adMaterialDetails.getImageUrl());
        adMaterial.setVideoUrl(adMaterialDetails.getVideoUrl());
        adMaterial.setLinkUrl(adMaterialDetails.getLinkUrl());
        adMaterial.setWidth(adMaterialDetails.getWidth());
        adMaterial.setHeight(adMaterialDetails.getHeight());
        adMaterial.setDuration(adMaterialDetails.getDuration());
        adMaterial.setCategory(adMaterialDetails.getCategory());
        adMaterial.setTargetInterest(adMaterialDetails.getTargetInterest());
        adMaterial.setBidPrice(adMaterialDetails.getBidPrice());
        adMaterial.setStatus(adMaterialDetails.getStatus());
        
        return adMaterialRepository.save(adMaterial);
    }

    /**
     * 删除广告素材
     *
     * @param id 广告素材ID
     */
    @Override
    public void deleteAdMaterial(Long id) {
        AdMaterial adMaterial = adMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告素材未找到"));
        adMaterialRepository.delete(adMaterial);
    }
}