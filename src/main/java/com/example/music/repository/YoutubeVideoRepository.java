package com.example.music.repository;

import com.example.music.entity.YoutubeVideo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, String> {
    Optional<YoutubeVideo> findByVideoUrl(String videoUrl);

    @Modifying
    @Transactional
    @Query("UPDATE YoutubeVideo yv SET yv.playlistCount = yv.playlistCount + 1 WHERE yv.videoUrl = :videoUrl")
    void incrementPlaylistCount(@Param("videoUrl") String videoUrl);

    @Modifying
    @Transactional
    @Query("UPDATE YoutubeVideo yv SET yv.streamCount = yv.streamCount + 1 WHERE yv.videoUrl = :videoUrl")
    void incrementStreamCount(@Param("videoUrl") String videoUrl);

    @Modifying
    @Transactional
    @Query("UPDATE YoutubeVideo yv SET yv.playlistCount = yv.playlistCount - 1 WHERE yv.videoUrl = :videoUrl")
    void decrementPlaylistCount(@Param("videoUrl") String videoUrl);
}
