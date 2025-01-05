package com.example.music.ServiceImpl;

import com.example.music.Objects.YoutubeApiResponse;
import com.example.music.Objects.Item;
import com.example.music.Services.YoutubeService;
import com.example.music.entity.PlaylistEntry;
import com.example.music.entity.User;
import com.example.music.repository.PlaylistEntryRepository;
import com.example.music.repository.YoutubeVideoRepository;
import com.example.music.utils.UserContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import com.example.music.entity.YoutubeVideo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.PropertySource;

@Service
@PropertySource("classpath:secrets.properties")
public class YoutubeServiceImpl implements YoutubeService {

    @Autowired
    private PlaylistEntryRepository playlistEntryRepository;

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    @Value("${youtube.api.key}")
    private String apiKey;
    public List<YoutubeVideo> fetchVideos(String keyword) {
//        System.out.println(apiKey);
        String apiUrl = "https://www.googleapis.com/youtube/v3/search";
        List<YoutubeVideo> videos = new ArrayList<>();
        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            int n=2;
            String queryUrl = String.format(
                    "%s?q=%s&part=snippet&type=video&maxResults="+Integer.toString(n)+"&key=%s",
                    apiUrl, encodedKeyword, apiKey
            );
            Process process = new ProcessBuilder("curl", "-s", queryUrl).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());

//            // Parse JSON response using Jackson (ObjectMapper)
            ObjectMapper objectMapper = new ObjectMapper();
            YoutubeApiResponse response = objectMapper.readValue(json, YoutubeApiResponse.class);
            // Iterate over the "items" list to extract video details
            for (Item item : response.getItems()) {
                String title = item.getSnippet().getTitle();
                String videoId = item.getId().getVideoId();

                // Create YouTubeVideo object and add to the list
                YoutubeVideo video = new YoutubeVideo();
                video.setTitle(title);
                video.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);
                videos.add(video);
//                downloadAudio(video.getVideoUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

//    public void downloadAudio(String videoUrl) {
//
//        System.out.println(videoUrl);
//        String command = String.format("yt-dlp -x --audio-format mp3 -o 'downloads/%%(title)s.%%(ext)s' %s", videoUrl);
//        System.out.println(command);
//        Process process = null;
//        try {
//            process = Runtime.getRuntime().exec(command);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            process.waitFor();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public InputStreamResource streamAudio(String videoUrl) {
        try {
            String command = String.format("yt-dlp -x --audio-format mp3 -o - %s", videoUrl);
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            InputStream audioStream = process.getInputStream();

            InputStreamResource resource = new InputStreamResource(audioStream);

            return resource;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}