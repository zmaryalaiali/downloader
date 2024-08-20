package com.luilala.kandrhar.downloader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.luilala.kandrhar.downloader.adapter.FormatAdapter;
import com.luilala.kandrhar.downloader.model.FormatModel;
import com.luilala.kandrhar.downloader.model.NestedFormatModel;

import java.io.File;
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
    ExoPlayer exoPlayer;
    StyledPlayerView styledPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_format);

        progressBar = findViewById(R.id.progressBar_format);
        styledPlayerView = findViewById(R.id.playerView_format);
        progressBar.setVisibility(View.VISIBLE);
        tvDownloadTitle = findViewById(R.id.tv_download_title);
        recyclerViewOption = findViewById(R.id.recyclerView_option);

        styledPlayerView.setVisibility(View.GONE);

        String urlId = null;
        exoPlayer = new ExoPlayer.Builder(this).build();
        styledPlayerView.setPlayer(exoPlayer);

        recyclerViewOption.setLayoutManager(new LinearLayoutManager(this));

        formatModelList = new ArrayList<>();

        youtubeDownloader = new YoutubeDownloader();
        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {

            videoLink = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            // We have a valid link
            if (videoLink != null) {
                if (isYouTubeVideo(videoLink)) {
                    urlId = extractVideoId(videoLink);
                } else {
                    urlId = extractShortsId(videoLink);
                }

                getAllFormat(urlId);
            } else {
                Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                finish();
            }

        }

        if (getIntent().getBooleanExtra("islink", false)) {
            videoLink = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (isYouTubeVideo(videoLink)) {
                urlId = extractVideoId(videoLink);
            } else {
                urlId = extractShortsId(videoLink);
            }
            getAllFormat(urlId);
        }

        if (getIntent().getBooleanExtra("isId",false)){
            String  videoId = getIntent().getStringExtra("videoId");
            getAllFormat(videoId);
        }
    }

    // get video and audio formats
    void getAllFormat(String videoId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestVideoInfo videoRequest = new RequestVideoInfo(videoId);
                final String[] url = new String[1];
                videoRequest.callback(new YoutubeCallback<VideoInfo>() {
                    @Override
                    public void onFinished(VideoInfo data) {
                        List<AudioFormat> formatListAudio = data.audioFormats();
                        List<VideoWithAudioFormat> formatListVideo = data.videoWithAudioFormats();

                        title = data.details().title();
                        List<NestedFormatModel> nestedFormatModelListAudio = new ArrayList<>();
                        List<NestedFormatModel> nestedFormatModelListVideo = new ArrayList<>();
                        Set<String> set = new HashSet<>();

                        for (AudioFormat audioFormat : formatListAudio) {
                            //audioFormat
                            if (set.add(audioFormat.audioQuality().name())) {
                                nestedFormatModelListAudio.add(new NestedFormatModel(".mp3", audioFormat.audioQuality().name(), audioFormat.url()));
                            }

                        }
                        formatModelList.add(new FormatModel("Audio", nestedFormatModelListAudio));

                        for (VideoWithAudioFormat videoFormat : formatListVideo) {
                            if (set.add(videoFormat.qualityLabel())) {
                                nestedFormatModelListVideo.add(new NestedFormatModel(".mp4", videoFormat.qualityLabel(), videoFormat.url(), formatListAudio.get(0).url(), false));
                            }

                        }
                        formatModelList.add(new FormatModel("Video", nestedFormatModelListVideo));
                        url[0] = nestedFormatModelListVideo.get(nestedFormatModelListVideo.size()-1).getUrl();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(VideoFormatActivity.this, "some error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).async();

                Response<VideoInfo> response = youtubeDownloader.getVideoInfo(videoRequest);
                VideoInfo videoInfo = response.data();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        tvDownloadTitle.setText(title);
                        styledPlayerView.setVisibility(View.VISIBLE);
                        Log.d("TAG", "run: url : "+url[0]);
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url[0]));
                             exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.play();
                        exoPlayer.setPlayWhenReady(true);
                        FormatAdapter adapter = new FormatAdapter(VideoFormatActivity.this, formatModelList, title);
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

    public boolean isYouTubeVideo(String url) {
        return !(url.contains("shorts"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
        exoPlayer = null;
    }

    //    private String convertTime(long ms) {
//        String time;
//        long x, seconds, minutes, hours;
//        x = ms / 1000;
//        seconds = x % 60;
//        x /= 60;
//        minutes = x % 60;
//        x /= 60;
//        hours = x % 24;
//        if (hours != 0) {
//            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
//        } else {
//            time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
//        }
//
//        return time;
//    }


}