package com.workingagenda.democracydroid.core.object;

import androidx.annotation.NonNull;

/**
 * Created by fen on 12/9/15.
 */
public class Episode {

    private String title;
    private String audioUrl;
    private String videoUrl;
    private String imageUrl;
    private String url;
    private String description;
    private String pubDate;

    @NonNull
    public String toString() {
        return "\nEpisode: " + videoUrl + " " + pubDate
                + "\n" + audioUrl + " " + title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
