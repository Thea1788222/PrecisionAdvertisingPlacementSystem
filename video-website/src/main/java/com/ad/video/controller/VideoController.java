package com.ad.video.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

@RestController

public class VideoController {

    @GetMapping("/api/videos")
    public List<Map<String, Object>> getVideos() {
        return Arrays.asList(
                Map.of("id", "1", "title", "测试视频1", "thumbnailUrl", "http://example.com/thumb1.jpg"),
                Map.of("id", "2", "title", "测试视频2", "thumbnailUrl", "http://example.com/thumb2.jpg")
        );
    }
}