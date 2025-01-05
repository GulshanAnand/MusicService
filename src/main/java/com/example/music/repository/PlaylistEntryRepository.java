package com.example.music.repository;

import com.example.music.entity.PlaylistEntry;
import com.example.music.entity.User;
import com.example.music.entity.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

@Repository
public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, UUID> {

    @Query("SELECT p FROM PlaylistEntry p WHERE p.user = :user AND p.youtubeVideo = :youtubeVideo")
    PlaylistEntry findByUserAndVideoUrl(@Param("user") User user, @Param("youtubeVideo") YoutubeVideo youtubeVideo);
}
