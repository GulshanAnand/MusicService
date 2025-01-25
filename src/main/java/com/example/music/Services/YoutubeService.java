package com.example.music.Services;

import com.example.music.dto.YoutubeVideoDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface YoutubeService {

    public List<YoutubeVideoDto> fetchVideos(String keyword) ;

    public InputStreamResource streamAudio(String videoUrl, String title) ;

    void streamAudio(HttpServletResponse response, String videoUrl, String title);

}
