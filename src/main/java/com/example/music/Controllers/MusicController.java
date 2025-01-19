package com.example.music.Controllers;

import com.example.music.Services.PlaylistService;
import com.example.music.entity.YoutubeVideo;
import com.example.music.Services.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import com.example.music.dto.YoutubeVideoDto;

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
    public ResponseEntity<List<YoutubeVideo>> searchTrack(@PathVariable String trackName){
        List<YoutubeVideo> videos = youtubeService.fetchVideos(trackName);
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

    @PostMapping("/playlist")
    public ResponseEntity<Boolean> addToPlaylist(@RequestBody YoutubeVideo youtubeVideo){
        System.out.println(youtubeVideo.toString());
        Boolean isSaved = playlistService.saveToPlaylist(youtubeVideo);
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
    public ResponseEntity<List<YoutubeVideo>> getPlaylist(){
        return ResponseEntity.status(HttpStatus.OK).body(playlistService.getPlaylist());
    }

    @DeleteMapping("/playlist")
    public ResponseEntity<Boolean> RemoveFromPlaylist(@RequestBody YoutubeVideo youtubeVideo){
        playlistService.deleteTrack(youtubeVideo);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

}
