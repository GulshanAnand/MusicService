package com.example.music.Services;

import com.example.music.entity.YoutubeVideo;
import com.example.music.dto.YoutubeVideoDto;

import java.util.List;

public interface PlaylistService {
    Boolean saveToPlaylist(YoutubeVideoDto youtubeVideoDto);

    List<YoutubeVideoDto> getPlaylist();

    void removeFromPlaylist(YoutubeVideoDto youtubeVideoDto);
}
