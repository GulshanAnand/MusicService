package com.example.music.repository;

import com.example.music.entity.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, UUID> {
    YoutubeVideo findByVideoUrl(String videoUrl);
}
