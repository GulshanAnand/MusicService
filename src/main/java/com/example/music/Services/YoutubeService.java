package com.example.music.Services;

import com.example.music.Objects.YoutubeVideo;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface YoutubeService {

    public List<YoutubeVideo> fetchVideos(String keyword) ;
    public void downloadAudio(String videoUrl) ;
    public InputStreamResource streamAudio(String videoUrl) ;

    }
