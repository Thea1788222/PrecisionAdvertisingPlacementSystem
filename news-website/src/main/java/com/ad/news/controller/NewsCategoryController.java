package com.ad.news.controller;

import com.ad.news.model.NewsCategory;
import com.ad.news.service.NewsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 新闻类别控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class NewsCategoryController {
    
    @Autowired
    private NewsCategoryService categoryService;
    
    /**
     * 获取所有类别（主页和管理页面共用）
     */
    @GetMapping
    public ResponseEntity<List<NewsCategory>> getAllCategories() {
        List<NewsCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 根据ID获取类别
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsCategory> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建类别（管理页面用）
     */
    @PostMapping("/admin")
    public ResponseEntity<NewsCategory> createCategory(@RequestBody NewsCategory category) {
        try {
            NewsCategory created = categoryService.createCategory(category);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新类别（管理页面用）
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<NewsCategory> updateCategory(@PathVariable Long id, @RequestBody NewsCategory category) {
        try {
            NewsCategory updated = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除类别（管理页面用）
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
