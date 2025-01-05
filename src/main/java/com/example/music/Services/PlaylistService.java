package com.example.music.Services;

import com.example.music.entity.YoutubeVideo;

import java.util.List;

public interface PlaylistService {
    Boolean saveToPlaylist(YoutubeVideo youtubeVideo);

    List<YoutubeVideo> getPlaylist();

    void deleteTrack(YoutubeVideo youtubeVideo);
}
