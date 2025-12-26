package com.ad.management.service;

import com.ad.management.model.AdPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface AdPositionService {

    /**
     * 获取所有广告位置（分页）
     *
     * @param pageable 分页参数
     * @return 所有广告位置的分页列表
     */
    Page<AdPosition> getAllAdPositions(Pageable pageable);

    /**
     * 获取所有广告位置
     *
     * @param website 网站名称
     * @return 所有广告位置的列表
     */
    List<AdPosition> getAllAdPositions(String website);

    /**
     * 根据条件获取广告位置列表（分页）
     *
     * @param website 网站名称
     * @param positionKey 位置标识
     * @param positionName 位置名称
     * @param pageable 分页参数
     * @return 符合条件的广告位置分页列表
     */
    Page<AdPosition> getAdPositionsByConditions(String website, String positionKey, String positionName, Pageable pageable);

    /**
     * 创建广告位置
     *
     * @param adPosition 广告位置对象
     * @return 创建的广告位置对象
     */
    AdPosition createAdPosition(AdPosition adPosition);

    /**
     * 根据ID获取广告位置
     *
     * @param id 广告位置ID
     * @return 匹配的广告位置对象
     */
    Optional<AdPosition> getAdPositionById(Long id);

    /**
     * 更新广告位置
     *
     * @param id                  广告位置ID
     * @param adPositionDetails   广告位置详情对象
     * @return 更新的广告位置对象
     */
    AdPosition updateAdPosition(Long id, AdPosition adPositionDetails);

    /**
     * 删除广告位置
     *
     * @param id 广告位置ID
     */
    void deleteAdPosition(Long id);
}