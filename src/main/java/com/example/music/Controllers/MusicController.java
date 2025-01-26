package com.example.music.Controllers;

import com.example.music.Services.PlaylistService;
import com.example.music.entity.YoutubeVideo;
import com.example.music.Services.YoutubeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import com.example.music.dto.YoutubeVideoDto;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/music")
public class MusicController {

    @Autowired
    private final YoutubeService youtubeService;

    @Autowired
    private final PlaylistService playlistService;

    public MusicController(YoutubeService youtubeService, PlaylistService playlistService) {
        this.youtubeService = youtubeService;
        this.playlistService = playlistService;
    }

    @GetMapping("/")
    public String HealthCheck(){
        return "HELLO";
    }

    @GetMapping("/search/{trackName}")
    public ResponseEntity<List<YoutubeVideoDto>> searchTrack(@PathVariable String trackName){
        List<YoutubeVideoDto> videos = youtubeService.fetchVideos(trackName);
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }

    @GetMapping("/top-tracks")
    public ResponseEntity<List<List<YoutubeVideoDto>>> getCharts(){
        List<List<YoutubeVideoDto>> videos = youtubeService.getCharts();
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadTrackOriginal(@RequestBody YoutubeVideoDto youtubeVideoDto){
        long startTime = System.nanoTime();
        InputStreamResource audioStream = youtubeService.streamAudio(youtubeVideoDto.getVideoUrl(), youtubeVideoDto.getTitle());
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInSeconds = duration / 1_000_000.0;
        System.out.println("total stream execution time: " + durationInSeconds + " milliseconds");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audioStream);

    }

    @PostMapping("/stream")
    public ResponseEntity<StreamingResponseBody> streamTrack(@RequestBody YoutubeVideoDto youtubeVideoDto) {
        String youtubeUrl = youtubeVideoDto.getVideoUrl();
        String command = String.format("yt-dlp -x --audio-format mp3 -o - %s", youtubeUrl);
        StreamingResponseBody responseBody = outputStream -> {
            ProcessBuilder processBuilder = new ProcessBuilder(
                command
            );

            Process process = processBuilder.start();
            
            try (
                InputStream inputStream = new BufferedInputStream(process.getInputStream());
                OutputStream os = outputStream
            ) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        return ResponseEntity.ok()
            .header("Content-Type", "audio/mpeg")
            .body(responseBody);
    }

    @PostMapping("/playlist")
    public ResponseEntity<Boolean> addToPlaylist(@RequestBody YoutubeVideoDto youtubeVideoDto){
        System.out.println(youtubeVideoDto.toString());
        Boolean isSaved = playlistService.saveToPlaylist(youtubeVideoDto);
        if(Objects.isNull(isSaved)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
        if(isSaved){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(true);
        }
        else{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(false);
        }
    }

    @GetMapping("/playlist")
    public ResponseEntity<List<YoutubeVideoDto>> getPlaylist(){
        return ResponseEntity.status(HttpStatus.OK).body(playlistService.getPlaylist());
    }

    @DeleteMapping("/playlist")
    public ResponseEntity<Boolean> RemoveFromPlaylist(@RequestBody YoutubeVideoDto youtubeVideoDto){
        playlistService.removeFromPlaylist(youtubeVideoDto);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

}
