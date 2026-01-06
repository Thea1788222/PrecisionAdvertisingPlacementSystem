package com.ad.news.service;

import com.ad.news.model.News;
import com.ad.news.model.NewsCategory;
import com.ad.news.repository.NewsRepository;
import com.ad.news.repository.NewsCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 新闻服务类
 */
@Service
public class NewsService {
    
    @Autowired
    private NewsRepository newsRepository;
    
    @Autowired
    private NewsCategoryRepository categoryRepository;
    
    /**
     * 获取最新20条新闻
     */
    public List<News> getLatestNews(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return newsRepository.findTop20ByOrderByPublishTimeDesc(pageable);
    }
    
    /**
     * 根据ID获取新闻详情
     */
    public Optional<News> getNewsById(Long id) {
        Optional<News> news = newsRepository.findByIdWithCategory(id);
        if (news.isPresent()) {
            // 增加浏览次数
            News n = news.get();
            n.setViews(n.getViews() + 1);
            newsRepository.save(n);
        }
        return news;
    }
    
    /**
     * 根据类别ID获取新闻（分页）
     */
    public Page<News> getNewsByCategory(Long categoryId, Pageable pageable) {
        return newsRepository.findByCategoryIdOrderByPublishTimeDesc(categoryId, pageable);
    }
    
    /**
     * 搜索新闻（分页，精确匹配）
     */
    public Page<News> searchNews(String keyword, Pageable pageable) {
        // 尝试将关键词解析为日期
        String dateKeyword = null;
        try {
            // 尝试多种日期格式
            java.time.format.DateTimeFormatter[] formatters = {
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy/M/d"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日")
            };

            for (var formatter : formatters) {
                try {
                    LocalDate.parse(keyword, formatter);
                    dateKeyword = keyword;
                    break;
                } catch (Exception e) {
                    // 继续尝试下一个格式
                }
            }
        } catch (Exception e) {
            // 如果无法解析为日期，则dateKeyword保持为null
        }

        return newsRepository.searchNews(keyword, dateKeyword, pageable);
    }
    
    /**
     * 创建新闻
     */
    @Transactional
    public News createNews(News news) {
        // 验证类别是否存在
        NewsCategory category = categoryRepository.findById(news.getCategoryId())
            .orElseThrow(() -> new RuntimeException("类别不存在: " + news.getCategoryId()));
        
        news.setCategory(category);
        return newsRepository.save(news);
    }
    
    /**
     * 更新新闻
     */
    @Transactional
    public News updateNews(Long id, News news) {
        News existing = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("新闻不存在: " + id));
        
        // 如果修改了类别，验证新类别是否存在
        if (!existing.getCategoryId().equals(news.getCategoryId())) {
            NewsCategory category = categoryRepository.findById(news.getCategoryId())
                .orElseThrow(() -> new RuntimeException("类别不存在: " + news.getCategoryId()));
            existing.setCategory(category);
        }
        
        existing.setTitle(news.getTitle());
        existing.setContent(news.getContent());
        existing.setAuthor(news.getAuthor());
        existing.setPublishTime(news.getPublishTime());
        
        return newsRepository.save(existing);
    }
    
    /**
     * 删除新闻
     */
    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
    
    /**
     * 获取所有新闻（分页，用于管理页面）
     */
    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAllWithCategory(pageable);
    }
}
