package com.ad.news.repository;

import com.ad.news.model.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 新闻类别数据访问接口
 */
@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {
    
    /**
     * 根据名称查找类别
     */
    Optional<NewsCategory> findByName(String name);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
}
