package com.example.music.Services;

import com.example.music.Objects.Track;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface TrackService {

    public List<Track> getAllTracks();

    public List<Track> getTrack(String name);

    public boolean addTrack(Track track);

}
