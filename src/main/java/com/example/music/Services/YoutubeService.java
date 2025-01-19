package com.example.music.Services;

import com.example.music.entity.YoutubeVideo;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface YoutubeService {

    public List<YoutubeVideo> fetchVideos(String keyword) ;

    public InputStreamResource streamAudio(String videoUrl, String title) ;

}
