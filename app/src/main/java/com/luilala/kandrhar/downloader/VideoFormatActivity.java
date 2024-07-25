package com.luilala.kandrhar.downloader;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.luilala.kandrhar.downloader.adapter.FormatAdapter;
import com.luilala.kandrhar.downloader.model.FormatModel;
import com.luilala.kandrhar.downloader.model.NestedFormatModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoFormatActivity extends AppCompatActivity {
    ProgressBar progressBar;
    YoutubeDownloader youtubeDownloader;

    List<FormatModel> formatModelList;
    RecyclerView recyclerViewOption;

    TextView tvDownloadTitle;
    String videoLink;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_format);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
        tvDownloadTitle = findViewById(R.id.tv_download_title);
        recyclerViewOption = findViewById(R.id.recyclerView_option);

        recyclerViewOption.setLayoutManager(new LinearLayoutManager(this));


        formatModelList = new ArrayList<>();


        youtubeDownloader = new YoutubeDownloader();


        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {

            videoLink = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if (videoLink != null) {
                if (isYouTubeVideo(videoLink)) {
                    getAllFormat(extractVideoId(videoLink));
                    Toast.makeText(this, extractShortsId(videoLink), Toast.LENGTH_SHORT).show();
                } else {
                    getAllFormat(extractShortsId(videoLink));
                    Toast.makeText(this, extractShortsId(videoLink)  + "video ID", Toast.LENGTH_SHORT).show();

                }

                Toast.makeText(this, "Done some thing", Toast.LENGTH_LONG).show();


                // We have a valid link
//                getYoutubeDownloadUrl(videoLink);
//                showDownloadList();
            } else {
                Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                finish();
            }


        }

        if (getIntent().getBooleanExtra("islink", false)) {
            videoLink = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (isYouTubeVideo(videoLink)) {
                getAllFormat(extractVideoId(videoLink));
                Toast.makeText(this, extractShortsId(videoLink), Toast.LENGTH_SHORT).show();
            } else {
                getAllFormat(extractShortsId(videoLink));
                Toast.makeText(this, extractShortsId(videoLink)  + "video ID", Toast.LENGTH_SHORT).show();

            }

        }
    }


    void getAllFormat(String videoId) {

//        Toast.makeText(this, "Done some thing", Toast.LENGTH_LONG).show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestVideoInfo videoRequest = new RequestVideoInfo(videoId);
                Response<VideoInfo> response = youtubeDownloader.getVideoInfo(videoRequest);
                VideoInfo videoInfo = response.data();

                List<AudioFormat> formatListAudio = videoInfo.audioFormats();
                List<VideoFormat> formatListVideo = videoInfo.videoFormats();

                title = videoInfo.details().title();
                List<NestedFormatModel> nestedFormatModelListAudio = new ArrayList<>();
                List<NestedFormatModel> nestedFormatModelListVideo = new ArrayList<>();
                Set<String> set = new HashSet<>();

                for (AudioFormat audioFormat : formatListAudio) {
                    //audioFormat
                    if (set.add(audioFormat.audioQuality().name())) {
                        String time = convertTime(audioFormat.duration());
                        nestedFormatModelListAudio.add(new NestedFormatModel(".mp3", audioFormat.audioQuality().name(), audioFormat.url()));
                    }

                }
                formatModelList.add(new FormatModel("Audio", nestedFormatModelListAudio));
                set = new HashSet<>();

                for (VideoFormat videoFormat : formatListVideo) {
                    if (set.add(videoFormat.qualityLabel())) {
                        String time = convertTime(videoFormat.duration());
                        nestedFormatModelListVideo.add(new NestedFormatModel(".mp4", videoFormat.qualityLabel(), videoFormat.url()));
                    }


                }
                formatModelList.add(new FormatModel("Video", nestedFormatModelListVideo));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        tvDownloadTitle.setText(title);
                        FormatAdapter adapter = new FormatAdapter(VideoFormatActivity.this, formatModelList, videoInfo.details().title());
                        adapter.notifyDataSetChanged();
                        recyclerViewOption.setAdapter(adapter);


                    }
                });

            }
        }).start();
    }

    // Method to extract video ID from YouTube video URL
    private String extractVideoId(String videoUrl) {
        String videoId = null;
        if (videoUrl != null && videoUrl.trim().length() > 0) {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(videoUrl);
            if (matcher.find()) {
                videoId = matcher.group();
            }
        }
        return videoId;
    }

    public static String extractShortsId(String url) {
        String pattern = "https://youtube.com/shorts/([^?]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    public boolean isYouTubeShort(String url) {

        String pattern = "^https://www\\.youtube\\.com/shorts/[a-zA-Z0-9_-]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        return m.find();
//        String shortRegex = "https://www\\.youtube\\.com/shorts/([a-zA-Z0-9_-]+)";
//        Pattern pattern = Pattern.compile(shortRegex);
//        Matcher matcher = pattern.matcher(url);
//        return matcher.matches();
    }

    public boolean isYouTubeVideo(String url) {
        return !(url.contains("shorts"));
    }


    private String convertTime(long ms) {
        String time;
        long x, seconds, minutes, hours;
        x = ms / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0) {
            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        } else {
            time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }

        return time;
    }


}