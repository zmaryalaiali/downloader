package com.luilala.kandrhar.downloader.model;

public class DownloadModel {

    public String url;
    public String videoTitle;
    public String type;
    public String audioUrl;

    public DownloadModel(String url, String videoTitle , String type) {
        this.url = url;
        this.videoTitle = videoTitle;
        this.type = type;
    }

    public DownloadModel(String url, String videoTitle, String type, String audioUrl) {
        this.url = url;
        this.videoTitle = videoTitle;
        this.type = type;
        this.audioUrl = audioUrl;
    }
}
