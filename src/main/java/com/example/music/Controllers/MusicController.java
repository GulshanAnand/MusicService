package com.example.music.Controllers;

import com.example.music.Objects.Track;
import com.example.music.Objects.YoutubeVideo;
import com.example.music.Services.TrackService;
import com.example.music.Services.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
//import org.springframework.web.bind.annotation.CrossOrigin;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



//@CrossOrigin(origins = "http://192.168.56.1:3001")
@RestController
@RequestMapping(path = "/music")
@CrossOrigin(origins = "http://192.168.0.123:3001")
public class MusicController {

    @Autowired
    private final TrackService trackService;

    @Autowired
    private final YoutubeService youtubeService;

    public MusicController(TrackService trackService, YoutubeService youtubeService) {
        this.trackService = trackService;
        this.youtubeService = youtubeService;
    }

    @GetMapping("/")
    public String HealthCheck(){
        return "HELLO";
    }

    @GetMapping("/track/{trackName}")
    public ResponseEntity<List<Track>> getMusic(@PathVariable String trackName) {
        return ResponseEntity.status(HttpStatus.OK).body(trackService.getTrack(trackName));
    }

    @GetMapping("/track")
    public ResponseEntity<List<Track>> getAllTracks() {
        return ResponseEntity.status(HttpStatus.OK).body(trackService.getAllTracks());
    }

    @PostMapping("/track")
    public ResponseEntity<Boolean> postTrack(@RequestBody Track track){
        return ResponseEntity.status(HttpStatus.CREATED).body(trackService.addTrack(track));
    }

    @GetMapping("/search/{trackName}")
    public ResponseEntity<List<YoutubeVideo>> searchTrack(@PathVariable String trackName){
        List<YoutubeVideo> videos = youtubeService.fetchVideos(trackName);
//        for(YoutubeVideo youtubeVideo : videos){
//            youtubeService.downloadAudio(youtubeVideo.getVideoUrl());
//        }
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }

//    @PostMapping("/download")
//    public ResponseEntity<Boolean> downloadTrackOriginal(@RequestBody String trackName){
//        youtubeService.downloadAudio(trackName);
////        return ResponseEntity.ok()
////                .contentType(MediaType.valueOf("audio/mpeg"))
////                .body(Boolean.TRUE);
//        return ResponseEntity.status(HttpStatus.OK).body(Boolean.TRUE);
//    }

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadTrackOriginal(@RequestBody String trackName){
        InputStreamResource audioStream = youtubeService.streamAudio(trackName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audioStream);

    }
}
