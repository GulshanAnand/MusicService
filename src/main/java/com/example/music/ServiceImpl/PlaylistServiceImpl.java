package com.example.music.ServiceImpl;

import com.example.music.Services.PlaylistService;
import com.example.music.adapters.YoutubeVideoAdapter;
import com.example.music.dto.YoutubeVideoDto;
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
import java.util.Optional;

import static com.example.music.adapters.YoutubeVideoAdapter.convertToDto;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistEntryRepository playlistEntryRepository;

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    @Override
    public Boolean saveToPlaylist(YoutubeVideoDto youtubeVideoDto) {
        try {
            PlaylistEntry playlistEntry = new PlaylistEntry();
            Optional<YoutubeVideo> existingYoutubeVideo = youtubeVideoRepository.findByVideoUrl(youtubeVideoDto.getVideoUrl());
            YoutubeVideo savedYoutubeVideo = null;

            if(existingYoutubeVideo.isEmpty()){
                savedYoutubeVideo = youtubeVideoRepository.save(YoutubeVideo.builder()
                        .title(youtubeVideoDto.getTitle())
                        .videoUrl(youtubeVideoDto.getVideoUrl())
                        .playlistCount(1)
                        .streamCount(0)
                        .build());
            }
            else{
                youtubeVideoRepository.incrementPlaylistCount(youtubeVideoDto.getVideoUrl());
                savedYoutubeVideo = youtubeVideoRepository.findByVideoUrl(youtubeVideoDto.getVideoUrl()).get();
            }

            playlistEntry.setYoutubeVideo(savedYoutubeVideo);
            User user = UserContextHolder.getCurrentUser();
            playlistEntry.setUser(user);
            PlaylistEntry existingPlaylistEntry = playlistEntryRepository.findByUserAndVideoUrl(user, savedYoutubeVideo); // check if Optional can be used
            if(!Objects.isNull(existingPlaylistEntry)) return false;
            playlistEntryRepository.save(playlistEntry);
            return true;
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public List<YoutubeVideoDto> getPlaylist() {
        User user = UserContextHolder.getCurrentUser();
        return playlistEntryRepository.findAllByUser(user)
                .stream()
                .map(YoutubeVideoAdapter::convertToDto)
                .toList();
    }

    @Override
    public void removeFromPlaylist(YoutubeVideoDto youtubeVideoDto) {
        try {
            User user = UserContextHolder.getCurrentUser();
            playlistEntryRepository.deleteByUserAndVideoUrl(user, youtubeVideoDto.getVideoUrl());
            youtubeVideoRepository.decrementPlaylistCount(youtubeVideoDto.getVideoUrl());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
