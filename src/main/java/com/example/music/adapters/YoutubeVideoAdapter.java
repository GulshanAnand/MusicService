package com.example.music.adapters;

import com.example.music.dto.YoutubeVideoDto;
import com.example.music.entity.YoutubeVideo;

public class YoutubeVideoAdapter {
    public static YoutubeVideoDto convertToDto(YoutubeVideo youtubeVideo){
        return YoutubeVideoDto.builder()
                .videoUrl(youtubeVideo.getVideoUrl())
                .title(youtubeVideo.getTitle())
                .build();
    }
}