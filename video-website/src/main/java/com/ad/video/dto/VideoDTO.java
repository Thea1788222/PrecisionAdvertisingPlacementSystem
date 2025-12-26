package com.ad.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoDTO {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private String playUrl;
    private String coverUrl;
}
