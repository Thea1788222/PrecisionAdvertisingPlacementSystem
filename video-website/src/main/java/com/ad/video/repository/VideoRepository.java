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

    @Query("SELECT v FROM Video v WHERE " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "ORDER BY " +
           "CASE WHEN LOWER(v.title) LIKE LOWER(CONCAT(?1, '%')) THEN 1 " +
           "     WHEN LOWER(v.title) LIKE LOWER(CONCAT('%', ?1, '%')) THEN 2 " +
           "     WHEN LOWER(v.description) LIKE LOWER(CONCAT(?1, '%')) THEN 3 " +
           "     ELSE 4 END, " +
           "CASE WHEN LOWER(v.title) LIKE LOWER(CONCAT('%', ?1, '%')) THEN 1 ELSE 0 END DESC, " +
           "v.duration ASC")
    List<Video> searchVideos(String query);


}
