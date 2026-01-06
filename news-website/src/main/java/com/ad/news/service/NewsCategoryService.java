package com.ad.news.service;

import com.ad.news.model.NewsCategory;
import com.ad.news.repository.NewsCategoryRepository;
import com.ad.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 新闻类别服务类
 */
@Service
public class NewsCategoryService {
    
    @Autowired
    private NewsCategoryRepository categoryRepository;
    
    @Autowired
    private NewsRepository newsRepository;
    
    /**
     * 获取所有类别（按排序顺序）
     */
    public List<NewsCategory> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }
    
    /**
     * 根据ID获取类别
     */
    public Optional<NewsCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * 创建类别
     */
    @Transactional
    public NewsCategory createCategory(NewsCategory category) {
        // 检查名称是否已存在
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("类别名称已存在: " + category.getName());
        }
        return categoryRepository.save(category);
    }
    
    /**
     * 更新类别
     */
    @Transactional
    public NewsCategory updateCategory(Long id, NewsCategory category) {
        NewsCategory existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("类别不存在: " + id));
        
        // 如果修改了名称，检查新名称是否已存在
        if (!existing.getName().equals(category.getName()) && 
            categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("类别名称已存在: " + category.getName());
        }
        
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setSortOrder(category.getSortOrder());
        
        return categoryRepository.save(existing);
    }
    
    /**
     * 删除类别
     */
    @Transactional
    public void deleteCategory(Long id) {
        // 检查是否有新闻使用该类别
        long newsCount = newsRepository.countByCategoryId(id);
        if (newsCount > 0) {
            throw new RuntimeException("该类别下还有 " + newsCount + " 条新闻，无法删除");
        }
        
        categoryRepository.deleteById(id);
    }
}
