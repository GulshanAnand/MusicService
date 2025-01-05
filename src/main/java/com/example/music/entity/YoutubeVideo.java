package com.example.music.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video")
public class YoutubeVideo {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "video_id")
//    private UUID videoId;

    @Id
    @Column(name = "video_url")
    private String videoUrl;

    @NonNull
    private String title;

}