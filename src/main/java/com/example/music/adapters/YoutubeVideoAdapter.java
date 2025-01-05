package com.example.music.adapters;

import com.example.music.dto.YoutubeVideoDto;
import com.example.music.entity.YoutubeVideo;
import org.springframework.stereotype.Component;

@Component
public class YoutubeVideoAdapter {
    public YoutubeVideo convert(YoutubeVideoDto youtubeVideoDto) {
        return new YoutubeVideo(null, youtubeVideoDto.getTitle(), youtubeVideoDto.getVideoUrl());
    }
}
