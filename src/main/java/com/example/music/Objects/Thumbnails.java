package com.example.music.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnails {
    private ThumbnailInfo defaultThumbnail;
    private ThumbnailInfo medium;
    private ThumbnailInfo high;

    public ThumbnailInfo getDefaultThumbnail() {
        return defaultThumbnail;
    }

    public void setDefaultThumbnail(ThumbnailInfo defaultThumbnail) {
        this.defaultThumbnail = defaultThumbnail;
    }

    public ThumbnailInfo getMedium() {
        return medium;
    }

    public void setMedium(ThumbnailInfo medium) {
        this.medium = medium;
    }

    public ThumbnailInfo getHigh() {
        return high;
    }

    public void setHigh(ThumbnailInfo high) {
        this.high = high;
    }
}
