package com.luilala.kandrhar.downloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.FormatField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.search.field.UploadDateField;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.luilala.kandrhar.downloader.adapter.AdapterHomeVideoRv;
import java.util.List;

public class HomeVideoActivity extends AppCompatActivity {

    String videoId , videoTitle;
    StyledPlayerView styledPlayerView ;
    TextView tvVideoTitle;
    RecyclerView rvVideo;
    ExoPlayer exoPlayer;
    ProgressBar progressBar;
    AdapterHomeVideoRv adapterHomeVideoRv ;
    RequestSearchResult searchResult ;

    YoutubeDownloader downloader;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_video);

        styledPlayerView = findViewById(R.id.homeActivity_videoView);
        tvVideoTitle = findViewById(R.id.homeActivity_videoTitle);
        rvVideo = findViewById(R.id.homeActivity_rvVideos);
        progressBar = findViewById(R.id.homeActivity_progressBar);

        rvVideo.setLayoutManager(new LinearLayoutManager(this));
        exoPlayer = new ExoPlayer.Builder(this).build();
        styledPlayerView.setPlayer(exoPlayer);


        Intent intent = getIntent();
        videoId = intent.getStringExtra("videoId");
        videoTitle = intent.getStringExtra("videoTitle");
        downloader = new YoutubeDownloader();

        if (CheckInternet.checkInternet(this)){
            Log.d(TAG, "onCreate: Internet Available");
            getAllFormat(videoId);
            loadVideos();

        }else {
            Log.d(TAG, "onCreate: no internet");
            // set text no internet available
        }
        tvVideoTitle.setText(videoTitle);



    }

    private void loadVideos(){
                new Thread(() -> {
            searchResult = new RequestSearchResult("Afghanistan")
                    .filter(UploadDateField.MONTH
                            , FormatField.HD
                            , TypeField.VIDEO);

            SearchResult result = downloader.search(searchResult).data();


// retrieve next result (20 items max per continuation)
//           if (result.hasContinuation()) {
//               RequestSearchContinuation nextRequest = new RequestSearchContinuation(result);
//               SearchResult  continuation = downloader.searchContinuation(nextRequest).data();
//           }
            List<SearchResultVideoDetails> videoDetails = result.videos();
            adapterHomeVideoRv = new AdapterHomeVideoRv(videoDetails , result , downloader);
            Log.d(TAG, "onViewCreated: list is fulling "+videoDetails.size());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG, "run: setAdapter done"+adapterHomeVideoRv.getItemCount());
                    rvVideo.setAdapter(adapterHomeVideoRv);
                    adapterHomeVideoRv.notifyDataSetChanged();
                }
            });

        }).start();
    }
    void getAllFormat(String videoId) {
        final String[] url = new String[1];

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestVideoInfo videoRequest = new RequestVideoInfo(videoId);
                Log.d(TAG, "run: video player");

                Response<VideoInfo> response = downloader.getVideoInfo(videoRequest);
                VideoInfo videoInfo = response.data();
                List<VideoWithAudioFormat> formatListVideo = videoInfo.videoWithAudioFormats();
                url[0] = formatListVideo.get(formatListVideo.size()-1).url();
                Log.d(TAG, "run: url video : "+url[0]);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        styledPlayerView.setVisibility(View.VISIBLE);
                        Log.d("TAG", "run: url : "+url[0]);
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url[0]));
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.play();
                        exoPlayer.setPlayWhenReady(true);

                    }
                });

            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
        exoPlayer = null;
    }
}