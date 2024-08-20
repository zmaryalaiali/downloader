package com.luilala.kandrhar.downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import android.widget.Toast;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.luilala.kandrhar.downloader.folder.Folder;

import java.io.File;

public class VideoPlayerActivity extends AppCompatActivity {

    private TextView tvVideoTitle;
    ExoPlayer exoPlayer;
    StyledPlayerView styledPlayerView;
    private String videoName, videoPath;
    String audioPath;
    private static final String TAG = "VideoPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoName = getIntent().getStringExtra("videoName");
        videoPath = getIntent().getStringExtra("videoPath");
        styledPlayerView = findViewById(R.id.playerView);

        exoPlayer = new ExoPlayer.Builder(this).build();

        styledPlayerView.setPlayer(exoPlayer);

        if (!getIntent().getBooleanExtra("both", false)) {
            MediaItem item = MediaItem.fromUri(Uri.fromFile(new File(videoPath)));
            exoPlayer.setMediaItem(item);
            exoPlayer.prepare();
            exoPlayer.play();
            exoPlayer.setPlayWhenReady(true);
        } else {
            audioPath = getIntent().getStringExtra("audioPath");
            Toast.makeText(this, "Both", Toast.LENGTH_SHORT).show();
//            MediaSource fir = new ProgressiveMediaSource.Factory(new FileDataSource.Factory())
//                    .createMediaSource(MediaItem.fromUri("jdkjfk"));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(new FileDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(Uri.fromFile(new File(videoPath))));
            MediaSource audioSource = new ProgressiveMediaSource.Factory(new FileDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(Uri.fromFile(new File(audioPath))));
            MergingMediaSource mergedSource = new MergingMediaSource(true, videoSource, audioSource);
            exoPlayer.setMediaSource(mergedSource, true);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            merge(audioPath,videoPath,videoName);

        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
        exoPlayer = null;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("both",false)){
            File video = new File(videoPath);
            File audio = new File(audioPath);
            video.delete();
            audio.delete();
            Log.d(TAG, "onBackPressed: ");        }
        super.onBackPressed();
    }

    private void merge(String audioPath, String videoPath, String title ) {
        String[] c = {"-i",videoPath
                , "-i",audioPath
                , "-c:v", "copy", "-c:a", "aac", "-map", "0:v:0", "-map", "1:a:0", "-shortest",
                Folder.getFile(this).getPath()+"/"+title};
        mergeVideo(c);
    }

    private void mergeVideo(String[] co){
//        FFmpeg.executeAsync(co, new ExecuteCallback() {
//            @Override
//            public void apply( long executionId, int returnCode ) {
//                Log.d("hello" , "return  " + returnCode);
//                Log.d("hello" , "executionID  " + executionId);
//                Log.d("hello" , "FFMPEG  " +  new FFmpegExecution(executionId,co));
//
//            }
//        });
    }




    private String convertTime(int ms) {
        String time;
        int x, seconds, minutes, hours;
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