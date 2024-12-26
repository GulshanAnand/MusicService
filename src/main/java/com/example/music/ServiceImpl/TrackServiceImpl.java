package com.example.music.ServiceImpl;

import com.example.music.Objects.Track;
import com.example.music.Services.TrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrackServiceImpl implements TrackService {

    List<Track> tracks = new ArrayList<>();
    Map<String,List<Track>> trackMap = new HashMap<>();
    public List<Track> getAllTracks(){
        return tracks;
    };

    public List<Track> getTrack(String name){
        return trackMap.containsKey(name)?trackMap.get(name):new ArrayList<>();
    }

    public boolean addTrack(Track track){
        try{
            List<Track> temp = trackMap.get(track.getName());
            if(temp==null) temp= new ArrayList<>();
            temp.add(track);
            trackMap.put(track.getName(), temp);
            tracks.add(track);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
