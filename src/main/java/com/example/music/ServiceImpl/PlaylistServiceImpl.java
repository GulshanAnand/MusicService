package com.example.music.ServiceImpl;

import com.example.music.Services.PlaylistService;
import com.example.music.entity.PlaylistEntry;
import com.example.music.entity.User;
import com.example.music.entity.YoutubeVideo;
import com.example.music.repository.PlaylistEntryRepository;
import com.example.music.repository.YoutubeVideoRepository;
import com.example.music.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistEntryRepository playlistEntryRepository;

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    @Override
    public Boolean saveToPlaylist(YoutubeVideo youtubeVideo) {
        try {
            PlaylistEntry playlistEntry = new PlaylistEntry();
            YoutubeVideo existingYoutubeVideo = youtubeVideoRepository.findByVideoUrl(youtubeVideo.getVideoUrl());
            YoutubeVideo savedYoutubeVideo = Objects.isNull(existingYoutubeVideo)?youtubeVideoRepository.save(youtubeVideo):existingYoutubeVideo;
            playlistEntry.setYoutubeVideo(savedYoutubeVideo);
            User user = UserContextHolder.getCurrentUser();
            playlistEntry.setUser(user);
            PlaylistEntry existingPlaylistEntry = playlistEntryRepository.findByUserAndVideoUrl(user, savedYoutubeVideo);
            if(!Objects.isNull(existingPlaylistEntry)) return false;
            playlistEntryRepository.save(playlistEntry);
            return true;
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public List<YoutubeVideo> getPlaylist() {
        User user = UserContextHolder.getCurrentUser();
//        return playlistEntryRepository.findAllByUser(user);
        List<YoutubeVideo> playlistEntries = playlistEntryRepository.findAllByUser(user);
        System.out.println(playlistEntries);
        return playlistEntries;
    }

    @Override
    public void deleteTrack(YoutubeVideo youtubeVideo) {
        try {
            User user = UserContextHolder.getCurrentUser();
            playlistEntryRepository.deleteByUserAndVideoUrl(user, youtubeVideo.getVideoUrl());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
