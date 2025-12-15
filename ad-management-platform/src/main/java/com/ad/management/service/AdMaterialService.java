package com.ad.management.service;
import com.ad.management.model.AdMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AdMaterialService {

    /**
     * 获取所有广告素材
     *
     * @param advertiserId 广告商ID
     * @param type         素材类型
     * @param category     素材分类
     * @param status       素材状态
     * @param pageable     分页参数
     * @return 广告素材列表
     */
    Page<AdMaterial> getAllAdMaterials(Long advertiserId, String type, String category, Integer status, Pageable pageable);

    /**
     * 创建广告素材
     *
     * @param adMaterial 广告素材信息
     * @return 创建成功的广告素材
     */
    AdMaterial createAdMaterial(AdMaterial adMaterial);

    /**
     * 根据ID获取广告素材
     *
     * @param id 广告素材ID
     * @return 获取的广告素材
     */
    Optional<AdMaterial> getAdMaterialById(Long id);

    /**
     * 更新广告素材
     *
     * @param id                  广告素材ID
     * @param adMaterialDetails   更新的广告素材信息
     * @return 更新的广告素材
     */
    AdMaterial updateAdMaterial(Long id, AdMaterial adMaterialDetails);

    /**
     * 删除广告素材
     *
     * @param id 广告素材ID
     */
    void deleteAdMaterial(Long id);
}