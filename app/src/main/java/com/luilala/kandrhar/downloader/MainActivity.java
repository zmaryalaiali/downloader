package com.luilala.kandrhar.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Button btnDownload;
    EditText etVideoURL;

    private static final String TAG = "ali";
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etVideoURL = findViewById(R.id.et_video_url);
        btnDownload = findViewById(R.id.btn_download);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Utils.BASE_URL)
                .build();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = etVideoURL.getText().toString();
                if (!link.isEmpty()) {
//                    Intent intent = new Intent(MainActivity.this,DownlaodActivity.class);
//                    Toast.makeText(MainActivity.this, link, Toast.LENGTH_SHORT).show();
//                    intent.putExtra("android.intent.extra.TEXT",link);
//                    startActivity(intent);
//                    finish();
//                    getVideoInfo(extractVideoId(link));
                    getInfo(extractVideoId(link));

                }
            }
        });

    }

    private void getInfo(String id) {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id + "&key=AIzaSyAdQ0trXZw9je8oQjRh7NHm5jrdItlOe54";
        GetVidoe vidoe = retrofit.create(GetVidoe.class);
        Call<VideoModel> call = vidoe.getVideoInfo(url);
        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "is successfully "+response.body().getItems().get(0).getSnippet().getThumbnails().getStandard().getHeight()+" and ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "not successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable throwable) {

                Toast.makeText(MainActivity.this, "error  :  " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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

    private void getVideoInfo(String videoId) {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                // Set your API key here
                httpRequest.getHeaders().set("key", "AIzaSyAdQ0trXZw9je8oQjRh7NHm5jrdItlOe54");
            }
        }).setApplicationName("YourApp").build();

        try {
            YouTube.Videos.List list = youtube.videos().list(Collections.singletonList("snippet,contentDetails"));
            list.setId(Collections.singletonList(videoId));
            VideoListResponse response = list.execute();
            List<Video> videos = response.getItems();
            if (videos.size() > 0) {
                Video video = videos.get(0);
                String title = video.getSnippet().getTitle();
                String resolution = video.getContentDetails().getDimension();
//                String format = video.getContentDetails().getFormat();

                // Now you can use the extracted information as needed
                // For example, you can display it in a TextView
//                titleTextView.setText("Title: " + title);
//                resolutionTextView.setText("Resolution: " + resolution);
//                formatTextView.setText("Format: " + format);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class YouTubeDataApiTask extends AsyncTask<String, Void, Video> {

        private static final String API_KEY = "AIzaSyAdQ0trXZw9je8oQjRh7NHm5jrdItlOe54";

        @Override
        protected Video doInBackground(String... urls) {
            String videoUrl = urls[0];
            String videoId = videoUrl.substring(videoUrl.lastIndexOf("=") + 1);

            try {
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                        .setApplicationName("youtubeDownloader")
                        .build();

                YouTube.Videos.List videoList = youtube.videos()
                        .list(Collections.singletonList("snippet,contentDetails"))
                        .setId(Collections.singletonList(videoId));
                videoList.setKey(API_KEY);
                VideoListResponse videoResponse = videoList.execute();

                return videoResponse.getItems().get(0);
            } catch (GoogleJsonResponseException e) {
                Log.e("YouTubeDataApiTask", "Error fetching video information", e);
                return null;
            } catch (Exception e) {
                Log.e("YouTubeDataApiTask", "Error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Video video) {
            if (video != null) {
                String title = video.getSnippet().getTitle();
                String resolution = video.getContentDetails().getDefinition();
                String format = video.getContentDetails().getDimension();

                // Display video information (e.g., in a TextView)
                Log.i("YouTubeDataApiTask", "Title: " + title);
                Log.i("YouTubeDataApiTask", "Resolution: " + resolution);
                Log.i("YouTubeDataApiTask", "Format: " + format);
            } else {
                Log.e("YouTubeDataApiTask", "Error retrieving video information");
            }
        }
    }

}