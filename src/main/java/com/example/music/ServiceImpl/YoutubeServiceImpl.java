package com.example.music.ServiceImpl;

import com.example.music.Objects.YoutubeApiResponse;
import com.example.music.Objects.Item;
import com.example.music.Services.YoutubeService;
import com.example.music.adapters.YoutubeVideoAdapter;
import com.example.music.entity.User;
import com.example.music.utils.UserContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.example.music.entity.YoutubeVideo;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.music.dto.YoutubeVideoDto;
import com.example.music.repository.YoutubeVideoRepository;

import java.io.*;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            Optional<YoutubeVideo> youtubeVideo = youtubeVideoRepository.findByVideoUrl(videoUrl);
            if(youtubeVideo.isEmpty()){
                youtubeVideoRepository.save(YoutubeVideo.builder()
                        .videoUrl(videoUrl)
                        .title(title)
                        .playlistCount(0)
                        .streamCount(1)
                        .build());
            }
            else{
                youtubeVideoRepository.incrementStreamCount(videoUrl);
            }

            return resource;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void streamAudio(HttpServletResponse response, String videoUrl, String title) {
        try {

//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=audio.mp3");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            response.setContentType("audio/mpeg");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"audio-file.mp3\"");

            String command = String.format("yt-dlp -x --audio-format mp3 -o - %s", videoUrl);

            System.out.println(command);
            long startTime = System.nanoTime();

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            Process process = processBuilder.start();

            try (InputStream processOutput = process.getInputStream();
                 OutputStream responseOutput = response.getOutputStream()) {

                // Stream the process output to the HTTP response
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = processOutput.read(buffer)) != -1) {
                    responseOutput.write(buffer, 0, bytesRead);
                    responseOutput.flush(); // Ensure partial data is sent to the client
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Command failed with exit code: " + exitCode);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double durationInSeconds = duration / 1_000_000.0;
            System.out.println("yt-dlp execution time: " + durationInSeconds + " milliseconds");

//            Optional<YoutubeVideo> existingYoutubeVideo = youtubeVideoRepository.findByVideoUrl(videoUrl);
//            if(existingYoutubeVideo.isEmpty()){
//                System.out.println("empty");
//                youtubeVideoRepository.save(YoutubeVideo.builder()
//                        .videoUrl(videoUrl)
//                        .title(title)
//                        .streamCount(1)
//                        .playlistCount(0)
//                        .build());
//            }
//            else{
//                System.out.println("increased");
//                youtubeVideoRepository.incrementStreamCount(videoUrl);
//            }


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<List<YoutubeVideoDto>> getCharts() {
        Pageable pageable = PageRequest.of(0, 10);
        return Stream.of(
                youtubeVideoRepository.getTopStreams(pageable),
                youtubeVideoRepository.getTopStarredTracks(pageable)
        ).map(list -> list.stream().map(YoutubeVideoAdapter::convertToDto).toList()).toList();
    }

}