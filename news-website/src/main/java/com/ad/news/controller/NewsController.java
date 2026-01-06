package com.ad.news.controller;

import com.ad.news.model.News;
import com.ad.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 新闻控制器（主页和管理页面共用）
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 获取最新20条新闻（主页用）
     */
    @GetMapping("/latest")
    public ResponseEntity<List<News>> getLatestNews() {
        List<News> news = newsService.getLatestNews(20);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 根据ID获取新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据类别ID获取新闻（分页）
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<News>> getNewsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<News> news = newsService.getNewsByCategory(categoryId, pageable);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 搜索新闻（分页）
     */
    @GetMapping("/search")
    public ResponseEntity<Page<News>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<News> news = newsService.searchNews(keyword, pageable);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 获取所有新闻（分页，管理页面用）
     */
    @GetMapping("/admin/all")
    public ResponseEntity<Page<News>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<News> news = newsService.getAllNews(pageable);
        return ResponseEntity.ok(news);
    }
    
    /**
     * 创建新闻（管理页面用）
     */
    @PostMapping("/admin")
    public ResponseEntity<News> createNews(@RequestBody News news) {
        try {
            News created = newsService.createNews(news);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新新闻（管理页面用）
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody News news) {
        try {
            News updated = newsService.updateNews(id, news);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除新闻（管理页面用）
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
