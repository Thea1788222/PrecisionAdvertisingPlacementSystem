package com.ad.video.controller;

import com.ad.video.dto.VideoDTO;
import com.ad.video.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController implements WebMvcConfigurer {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/videos")
    public List<VideoDTO> getVideoList() {
        return videoService.listVideos();
    }

    @GetMapping("/videos/{id}")
    public VideoDTO getVideo(@PathVariable Long id) {
        return videoService.getVideoById(id);
    }

    // 静态资源映射保持不变
    @Value("${video.storage-path}")
    private String videoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + videoPath + "/");
    }
}
