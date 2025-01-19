package com.example.music.ServiceImpl;

import com.example.music.Objects.YoutubeApiResponse;
import com.example.music.Objects.Item;
import com.example.music.Services.YoutubeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import com.example.music.entity.YoutubeVideo;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.music.dto.YoutubeVideoDto;
import com.example.music.repository.YoutubeVideoRepository;

import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.PropertySource;

@Service
@PropertySource("classpath:secrets.properties")
public class YoutubeServiceImpl implements YoutubeService {

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    @Value("${youtube.api.key}")
    private String apiKey;
    public List<YoutubeVideoDto> fetchVideos(String keyword) {
        String apiUrl = "https://www.googleapis.com/youtube/v3/search";
        List<YoutubeVideoDto> videos = new ArrayList<>();
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            int n=10;
            String queryUrl = String.format(
                    "%s?q=%s&part=snippet&type=video&maxResults="+Integer.toString(n)+"&key=%s",
                    apiUrl, encodedKeyword, apiKey
            );
            Process process = new ProcessBuilder("curl", "-s", queryUrl).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            YoutubeApiResponse response = objectMapper.readValue(json, YoutubeApiResponse.class);
            for (Item item : response.getItems()) {
                String title = item.getSnippet().getTitle();
                String videoId = item.getId().getVideoId();
                videos.add(YoutubeVideoDto.builder()
                        .title(title)
                        .videoUrl("https://www.youtube.com/watch?v=" + videoId)
                        .build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

    public InputStreamResource streamAudio(String videoUrl, String title) {
        try {
            String command = String.format("yt-dlp -x --audio-format mp3 -o - %s", videoUrl);
            System.out.println(command);
            long startTime = System.nanoTime();
            Process process = Runtime.getRuntime().exec(command);
            InputStream audioStream = process.getInputStream();

            InputStreamResource resource = new InputStreamResource(audioStream);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double durationInSeconds = duration / 1_000_000.0;
            System.out.println("yt-dlp execution time: " + durationInSeconds + " milliseconds");

            Optional<YoutubeVideo> existingYoutubeVideo = youtubeVideoRepository.findByVideoUrl(videoUrl);
            if(existingYoutubeVideo.isEmpty()){
                System.out.println("empty");
                youtubeVideoRepository.save(YoutubeVideo.builder()
                        .videoUrl(videoUrl)
                        .title(title)
                        .streamCount(1)
                        .playlistCount(0)
                        .build());
            }
            else{
                System.out.println("increased");
                youtubeVideoRepository.incrementStreamCount(videoUrl);
            }


            return resource;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}