package com.ad.video.service;

import com.ad.video.dto.VideoDTO;
import com.ad.video.entity.Video;
import com.ad.video.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    /**
     * 查询所有视频
     */
    public List<VideoDTO> listVideos() {
        return videoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据视频ID查询单个视频
     */
    public VideoDTO getVideoById(Long id) {
        Video v = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return toDTO(v);
    }

    /**
     * 根据分类查询视频
     */
    public List<VideoDTO> getVideosByCategory(String category) {
        try {
            Video.VideoCategory videoCategory = Video.VideoCategory.valueOf(category.toUpperCase());
            return videoRepository.findByCategory(videoCategory).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Invalid category: " + category +
                ". Valid categories are: electronics, fashion, sports, home, food, travel, education, finance, health, beauty"
            );
        }
    }

    /**
     * 根据关键词搜索视频
     */
    public List<VideoDTO> searchVideos(String query) {
        if (query == null || query.trim().isEmpty()) {
            // 空搜索返回全部
            return listVideos();
        }

        String trimmedQuery = query.trim();
        List<Video> results = videoRepository.searchVideos(trimmedQuery);

        // 限制返回数量，防止一次返回太多数据
        if (results.size() > 100) {
            results = results.subList(0, 100);
        }

        return results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为DTO（方式A：直接透传数据库URL）
     */
    private VideoDTO toDTO(Video v) {
        return new VideoDTO(
                v.getId(),
                v.getTitle(),
                v.getDescription(),
                v.getDuration(),
                v.getPlayUrl(),   // 直接使用数据库里的完整URL
                v.getCoverUrl(),  // 直接使用数据库里的完整URL
                v.getCategory().toString()
        );
    }
}
