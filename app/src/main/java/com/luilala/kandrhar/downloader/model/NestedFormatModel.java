package com.luilala.kandrhar.downloader.model;

public class NestedFormatModel {
     private String format;
     private String quality;
     private String url;
     private String audioUrl;

     private boolean isSelect ;

    public NestedFormatModel(String format, String quality, String url) {
        this.format = format;
        this.quality = quality;
        this.url = url;
    }

    public NestedFormatModel(String format, String quality, String url, String audioUrl, boolean isSelect) {
        this.format = format;
        this.quality = quality;
        this.url = url;
        this.audioUrl = audioUrl;
        this.isSelect = isSelect;
    }

    public String getQuality() {
        return quality;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}
