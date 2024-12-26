package com.example.music.Objects;

import lombok.Data;
import lombok.NonNull;

@Data
public class Track {

    @NonNull
    private String name;
    @NonNull
    private String url;

    Track(@NonNull String name, @NonNull String url){
        this.name = name;
        this.url = url;
    }
}
