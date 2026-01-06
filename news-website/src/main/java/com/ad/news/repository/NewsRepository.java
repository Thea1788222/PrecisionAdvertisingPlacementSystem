package com.ad.news.repository;

import com.ad.news.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 新闻数据访问接口
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    /**
     * 根据类别ID查找新闻（分页）
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category WHERE n.category.id = :categoryId ORDER BY n.publishTime DESC")
    Page<News> findByCategoryIdOrderByPublishTimeDesc(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * 查找最新新闻（按发布时间倒序）
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category ORDER BY n.publishTime DESC")
    List<News> findTop20ByOrderByPublishTimeDesc(Pageable pageable);

    /**
     * 搜索新闻（精确匹配：标题或作者或日期完全一致）
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category WHERE n.title = :keyword OR n.author = :keyword OR DATE(n.publishTime) = DATE(:dateKeyword) ORDER BY n.publishTime DESC")
    Page<News> searchNews(@Param("keyword") String keyword, @Param("dateKeyword") String dateKeyword, Pageable pageable);
    
    /**
     * 根据ID查找新闻详情（包含category信息）
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category WHERE n.id = :id")
    Optional<News> findByIdWithCategory(@Param("id") Long id);

    /**
     * 获取所有新闻（包含category信息，用于管理页面）
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category ORDER BY n.publishTime DESC")
    Page<News> findAllWithCategory(Pageable pageable);

    /**
     * 根据类别ID统计新闻数量
     */
    long countByCategoryId(Long categoryId);
}
