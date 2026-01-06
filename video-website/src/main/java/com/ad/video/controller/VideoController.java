package com.ad.video.controller;

import com.ad.video.dto.VideoDTO;
import com.ad.video.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频 API 控制器
 * 负责视频列表、单条查询、按分类查询、搜索
 * 不处理静态资源映射
 */
@RestController
@RequestMapping("/api")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 查询全部视频列表
     * GET /api/videos
     */
    @GetMapping("/videos")
    public List<VideoDTO> getVideoList() {
        return videoService.listVideos();
    }

    /**
     * 根据视频 ID 查询单条视频
     * GET /api/videos/{id}
     */
    @GetMapping("/videos/{id}")
    public VideoDTO getVideo(@PathVariable Long id) {
        return videoService.getVideoById(id);
    }

    /**
     * 根据分类查询视频
     * GET /api/videos/category/{category}
     */
    @GetMapping("/videos/category/{category}")
    public List<VideoDTO> getVideosByCategory(@PathVariable String category) {
        return videoService.getVideosByCategory(category);
    }

    /**
     * 搜索视频
     * GET /api/videos/search?query=关键词
     */
    @GetMapping("/videos/search")
    public List<VideoDTO> searchVideos(@RequestParam String query) {
        return videoService.searchVideos(query);
    }
}
