package com.ad.video.repository;

import com.ad.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    @Query("SELECT v FROM Video v WHERE v.category = ?1")
    List<Video> findByCategory(Video.VideoCategory category);
}
