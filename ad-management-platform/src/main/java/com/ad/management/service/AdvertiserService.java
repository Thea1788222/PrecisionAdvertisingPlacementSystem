package com.ad.management.service;

import com.ad.management.model.Advertiser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AdvertiserService {

    /**
     * 获取所有广告主
     *
     * @param name    广告主名称（可选）
     * @param pageable 分页信息
     * @return 广告主分页列表
     */
    Page<Advertiser> getAllAdvertisers(String name, Pageable pageable);

    /**
     * 创建广告主
     *
     * @param advertiser 广告主信息
     * @return 创建成功的广告主
     */
    Advertiser createAdvertiser(Advertiser advertiser);

    /**
     * 根据ID获取广告主
     *
     * @param id 广告主ID
     * @return 匹配的广告主对象
     */
    Optional<Advertiser> getAdvertiserById(Long id);

    /**
     * 更新广告主信息
     *
     * @param id              广告主ID
     * @param advertiserDetails 更新的广告主信息
     * @return 更新后的广告主对象
     */
    Advertiser updateAdvertiser(Long id, Advertiser advertiserDetails);

    /**
     * 删除广告主
     *
     * @param id 广告主ID
     */
    void deleteAdvertiser(Long id);
}