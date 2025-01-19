package com.example.music.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video")
public class YoutubeVideo {

    @Id
    @Column(name = "video_url")
    private String videoUrl;

    private String title;

}