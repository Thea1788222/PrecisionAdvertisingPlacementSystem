package com.ad.management.service.impl;

import com.ad.management.model.AdPosition;
import com.ad.management.repository.AdPositionRepository;
import com.ad.management.service.AdPositionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdPositionServiceImpl implements AdPositionService {
    
    private final AdPositionRepository adPositionRepository;
    
    public AdPositionServiceImpl(AdPositionRepository adPositionRepository) {
        this.adPositionRepository = adPositionRepository;
    }

    /**
     * 获取所有广告位置
     *
     * @param website 网站名称
     * @return 所有广告位置的列表
     */
    @Override
    public List<AdPosition> getAllAdPositions(String website) {
        return adPositionRepository.findByWebsite(website);
    }
    
    /**
     * 创建广告位置
     *
     * @param adPosition 广告位置对象
     * @return 创建的广告位置对象
     */
    @Override
    public AdPosition createAdPosition(AdPosition adPosition) {
        return adPositionRepository.save(adPosition);
    }
    
    /**
     * 根据ID获取广告位置
     *
     * @param id 广告位置ID
     * @return 匹配的广告位置对象
     */
    @Override
    public Optional<AdPosition> getAdPositionById(Long id) {
        return adPositionRepository.findById(id);
    }
    
    /**
     * 更新广告位置
     *
     * @param id                  广告位置ID
     * @param adPositionDetails   广告位置详情对象
     * @return 更新的广告位置对象
     */
    @Override
    public AdPosition updateAdPosition(Long id, AdPosition adPositionDetails) {
        AdPosition adPosition = adPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告位未找到"));
        
        adPosition.setPositionName(adPositionDetails.getPositionName());
        adPosition.setWidth(adPositionDetails.getWidth());
        adPosition.setHeight(adPositionDetails.getHeight());
        adPosition.setDescription(adPositionDetails.getDescription());
        
        return adPositionRepository.save(adPosition);
    }

    /**
     * 删除广告位置
     *
     * @param id 广告位置ID
     */
    @Override
    public void deleteAdPosition(Long id) {
        AdPosition adPosition = adPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告位未找到"));
        adPositionRepository.delete(adPosition);
    }
}