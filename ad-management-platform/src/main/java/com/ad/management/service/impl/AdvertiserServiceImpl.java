package com.ad.management.service.impl;

import com.ad.management.model.Advertiser;
import com.ad.management.repository.AdvertiserRepository;
import com.ad.management.service.AdvertiserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdvertiserServiceImpl implements AdvertiserService {
    
    private final AdvertiserRepository advertiserRepository;
    
    public AdvertiserServiceImpl(AdvertiserRepository advertiserRepository) {
        this.advertiserRepository = advertiserRepository;
    }
    
    /**
     * 获取所有广告主
     *
     * @param name    广告主名称（可选）
     * @param pageable 分页信息
     * @return 广告主分页列表
     */
    @Override
    public Page<Advertiser> getAllAdvertisers(String name, Pageable pageable) {
        return advertiserRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    /**
     * 创建广告主
     *
     * @param advertiser 广告主信息
     * @return 创建成功的广告主
     */
    @Override
    public Advertiser createAdvertiser(Advertiser advertiser) {
        return advertiserRepository.save(advertiser);
    }
    
    /**
     * 根据ID获取广告主
     *
     * @param id 广告主ID
     * @return 匹配的广告主对象
     */
    @Override
    public Optional<Advertiser> getAdvertiserById(Long id) {
        return advertiserRepository.findById(id);
    }
    
    /**
     * 更新广告主信息
     *
     * @param id              广告主ID
     * @param advertiserDetails 更新的广告主信息
     * @return 更新后的广告主对象
     */
    @Override
    public Advertiser updateAdvertiser(Long id, Advertiser advertiserDetails) {
        Advertiser advertiser = advertiserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告商未找到"));
        
        advertiser.setName(advertiserDetails.getName());
        advertiser.setContact(advertiserDetails.getContact());
        advertiser.setEmail(advertiserDetails.getEmail());
        advertiser.setStatus(advertiserDetails.getStatus());
        
        return advertiserRepository.save(advertiser);
    }
    
    /**
     * 删除广告主
     *
     * @param id 广告主ID
     */
    @Override
    public void deleteAdvertiser(Long id) {
        Advertiser advertiser = advertiserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告商未找到"));
        advertiserRepository.delete(advertiser);
    }
}