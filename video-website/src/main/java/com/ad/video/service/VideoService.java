package com.ad.video.service;

import com.ad.video.dto.VideoDTO;
import com.ad.video.entity.Video;
import com.ad.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    
    @Value("${video.public-url-prefix}")
    private String publicUrlPrefix;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<VideoDTO> listVideos() {
        return videoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VideoDTO getVideoById(Long id) {
        Video v = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return toDTO(v);
    }

    private VideoDTO toDTO(Video v) {
        // 从原URL中提取文件名（不包含斜杠）
        String playFileName = v.getPlayUrl().substring(v.getPlayUrl().lastIndexOf("/") + 1);
        String coverFileName = v.getCoverUrl().substring(v.getCoverUrl().lastIndexOf("/") + 1);
        
        // 构建正确的URL
        String playUrl = publicUrlPrefix + playFileName;
        String coverUrl = publicUrlPrefix + coverFileName;
        
        return new VideoDTO(
                v.getId(),
                v.getTitle(),
                v.getDescription(),
                v.getDuration(),
                playUrl,
                coverUrl
        );
    }
}
