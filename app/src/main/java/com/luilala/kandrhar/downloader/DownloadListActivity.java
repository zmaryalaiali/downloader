package com.luilala.kandrhar.downloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.luilala.kandrhar.downloader.model.DownloadModel;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadListActivity extends AppCompatActivity implements ActionListener {

    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final long UNKNOWN_REMAINING_TIME = -1;
    private static final long UNKNOWN_DOWNLOADED_BYTES_PER_SECOND = 0;
    public static final String NOTIFICATION_ID = "downloaderNotificationID";
    private static final int GROUP_ID = "listGroup".hashCode();

    private static final int GROUP_ID_AUDIO = "listGroup".hashCode();
    static final String FETCH_NAMESPACE = "DownloadListActivity";

    private View mainView;
    private FileAdapter fileAdapter;
    List<FileAdapter.DownloadData> audioList;
    private Fetch fetch;
    private Fetch audioFetch;

    private static final String TAG = "DownloadListActivity";

    boolean isDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        setUpViews();
        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(4)
                .setNamespace(FETCH_NAMESPACE)
                .setNotificationManager(new DefaultFetchNotificationManager(this) {
                    @NonNull
                    @Override
                    public Fetch getFetchInstanceForNamespace(@NonNull String s) {
                        return fetch;
                    }
                })
                .build();
        audioFetch = Fetch.Impl.getInstance(fetchConfiguration);

        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        fetch.setGlobalNetworkType(NetworkType.ALL);
        audioFetch.setGlobalNetworkType(NetworkType.ALL);

        checkStoragePermissions();

        isDownload = getIntent().getBooleanExtra("isDownload", false);
        if (isDownload) {

                String url = getIntent().getStringExtra("url");
                Data.sampleUrls = new DownloadModel[]{new DownloadModel(url, getIntent().getStringExtra("title"), getIntent().getStringExtra("type"))};
                Toast.makeText(this, "downloading", Toast.LENGTH_SHORT).show();

        }
    }

    private void setUpViews() {
//        final SwitchCompat networkSwitch = findViewById(R.id.networkSwitch);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mainView = findViewById(R.id.activity_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
      new Thread(new Runnable() {
          @Override
          public void run() {
              fetch.getDownloadsInGroup(GROUP_ID, downloads -> {
                  final ArrayList<Download> list = new ArrayList<>(downloads);
                  Collections.sort(list, (first, second) -> Long.compare(first.getCreated(), second.getCreated()));
                  for (Download download : list) {
                      fileAdapter.addDownload(download);
                  }
              }).addListener(fetchListener);
          }
      }).start();
        // list for audio download with video
//       new Thread(new Runnable() {
//           @Override
//           public void run() {
//               Handler handler = new Handler(Looper.getMainLooper());
//               audioFetch.getDownloadsInGroup(GROUP_ID_AUDIO,downloads->{
//                   final ArrayList<Download> list = new ArrayList<>(downloads);
//                   Collections.sort(list, new Comparator<Download>() {
//                       @Override
//                       public int compare(Download o1, Download o2) {
//                           return Long.compare(o1.getCreated(),o2.getCreated());
//                       }
//                   });
//                   for (Download download : list){
//                       fileAdapter.addDownloadAudio(download);
//                   }
//               }).addListener(listenerAudio);
//           }
//       }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetch.removeListener(fetchListener);
//        audioFetch.removeListener(listenerAudio);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.close();
        audioFetch.close();
    }

    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {
            fileAdapter.addDownload(download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
            super.onError(download, error, throwable);
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
            fileAdapter.update(download, etaInMilliseconds, downloadedBytesPerSecond);
        }

        @Override
        public void onPaused(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onResumed(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onDeleted(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }
    };

    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            enqueueDownloads();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enqueueDownloads();
        } else {
            Snackbar.make(mainView, R.string.permission_not_enabled, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void enqueueDownloads() {
        final List<Request> requests = Data.getFetchRequestWithGroupId(GROUP_ID, this);
        fetch.enqueue(requests, updatedRequests -> {

        });

    }

    @Override
    public void onPauseDownload(int id) {
        fetch.pause(id);
    }

    @Override
    public void onResumeDownload(int id) {
        fetch.resume(id);
    }

    @Override
    public void onRemoveDownload(int id) {
        fetch.remove(id);
    }

    @Override
    public void onRetryDownload(int id) {
        fetch.retry(id);
    }

    public abstract class MyNotification extends DefaultFetchNotificationManager {

        public MyNotification(@NonNull Context context) {
            super(context);
        }
    }

    // this method for when user want to download video and audio to merge
    private void audioDownload(String audioUrl, String title, boolean withAudio) {


        final Request request = new Request(audioUrl, Data.getFilePath("ma4", title, this));
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        audioFetch.enqueue(request, new Func<Request>() {
            @Override
            public void call(@NonNull Request result) {
                Log.d(TAG, "call: downloading started");            }
        }, new Func<Error>() {
            @Override
            public void call(@NonNull Error result) {

                Log.d(TAG, "call: some error "+result.getThrowable().getMessage());            }
        });
    }

//    private FetchListener listenerAudio = new AbstractFetchListener() {
//        @Override
//        public void onAdded(@NonNull Download download) {
//            fileAdapter.addDownloadAudio(download);
//        }
//
//        @Override
//        public void onCancelled(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onCompleted(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onDeleted(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int totalBlocks) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onError(@NonNull Download download, @NonNull Error error, @androidx.annotation.Nullable Throwable throwable) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onPaused(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onProgress(@NonNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onQueued(@NonNull Download download, boolean waitingOnNetwork) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//        }
//
//        @Override
//        public void onRemoved(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onResumed(@NonNull Download download) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//
//        @Override
//        public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> downloadBlocks, int totalBlocks) {
//            fileAdapter.updateAudio(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
//
//        }
//    };
//
//    ActionListener actionListener = new ActionListener() {
//        @Override
//        public void onPauseDownload(int id) {
//            audioFetch.pause(id);
//        }
//
//        @Override
//        public void onResumeDownload(int id) {
//            audioFetch.resume(id);
//        }
//
//        @Override
//        public void onRemoveDownload(int id) {
//            audioFetch.remove(id);
//        }
//
//        @Override
//        public void onRetryDownload(int id) {
//            audioFetch.retry(id);
//        }
//    };


}