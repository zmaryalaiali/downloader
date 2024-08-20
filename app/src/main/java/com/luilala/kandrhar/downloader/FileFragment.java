package com.luilala.kandrhar.downloader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.luilala.kandrhar.downloader.folder.Folder;

import java.io.File;
import java.util.ArrayList;

public class FileFragment extends Fragment implements VideoRVAdapter.VideoClickInterface {

    ImageButton imageButton;

    private RecyclerView rvVideo;
    private ArrayList<VideoRVModel> videoRVModelArrayList;
    private VideoRVAdapter videoRVAdapter;
    private static final int STORAGE_PERMISSION = 101;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvVideo = view.findViewById(R.id.RVvideos);
        imageButton = view.findViewById(R.id.imageBtn_gotoDownload);
//        toolbar = view.findViewById(R.id.toolbar_home);
//
//        toolbar.setVisibility(View.GONE);


        videoRVModelArrayList = new ArrayList<>();
        videoRVAdapter = new VideoRVAdapter(videoRVModelArrayList, getContext(), this::onVideoClick);
        rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVideo.setAdapter(videoRVAdapter);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            getVideos();
        } else {
            getVideos();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DownloadListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "the app well not work without permission", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }

    private void getVideos() {

        File folder = Folder.getFile(getContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                if (folder.exists()) {
                    File[] files = folder.listFiles();
                    for (File file : files) {
                        Uri uri = Uri.fromFile(file);
                        Bitmap videothumbnail = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                        videoRVModelArrayList.add(new VideoRVModel(file.getName(), uri.getPath(), videothumbnail));
                       handler.post(new Runnable() {
                           @Override
                           public void run() {
                               videoRVAdapter.notifyDataSetChanged();
                           }
                       });
                    }
                }

            }
        }).start();

    }

    @Override
    public void onVideoClick(int position) {

        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
        intent.putExtra("videoName", videoRVModelArrayList.get(position).getName());
        intent.putExtra("videoPath", videoRVModelArrayList.get(position).getPath());
        startActivity(intent);

    }
}