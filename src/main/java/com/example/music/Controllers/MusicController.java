package com.example.music.Controllers;

import com.example.music.Objects.Track;
import com.example.music.Services.PlaylistService;
import com.example.music.entity.PlaylistEntry;
import com.example.music.entity.YoutubeVideo;
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


import java.util.List;
import java.util.Objects;


//@CrossOrigin(origins = "http://192.168.56.1:3001")
@RestController
@RequestMapping(path = "/music")
public class MusicController {

    @Autowired
    private final TrackService trackService;

    @Autowired
    private final YoutubeService youtubeService;

    @Autowired
    private final PlaylistService playlistService;

    public MusicController(TrackService trackService, YoutubeService youtubeService, PlaylistService playlistService) {
        this.trackService = trackService;
        this.youtubeService = youtubeService;
        this.playlistService = playlistService;
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

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadTrackOriginal(@RequestBody String trackName){
        InputStreamResource audioStream = youtubeService.streamAudio(trackName);
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
