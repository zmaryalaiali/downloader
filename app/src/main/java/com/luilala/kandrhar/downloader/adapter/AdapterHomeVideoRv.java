package com.luilala.kandrhar.downloader.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.luilala.kandrhar.downloader.CheckInternet;
import com.luilala.kandrhar.downloader.HomeVideoActivity;
import com.luilala.kandrhar.downloader.R;
import com.luilala.kandrhar.downloader.VideoFormatActivity;
import com.luilala.kandrhar.downloader.VideoPlayerActivity;

import java.util.List;

public class AdapterHomeVideoRv extends RecyclerView.Adapter<AdapterHomeVideoRv.VideoHolder> {

    List<SearchResultVideoDetails> list;
    private static final String TAG = "MainActivity";
    SearchResult result;
    YoutubeDownloader downloader;
    private Context context;

    public AdapterHomeVideoRv(List<SearchResultVideoDetails> list, SearchResult result, YoutubeDownloader downloader) {
        this.list = list;
        this.result = result;
        this.downloader = downloader;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_home, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        SearchResultVideoDetails videoDetails = list.get(position);
        if (position == list.size() - 4) {
            setContinuation();
        }

        holder.tvVideoTitle.setText(videoDetails.title());
        List<String> thumbnails = videoDetails.thumbnails();
        String url = thumbnails.get(thumbnails.size() - 1);
        Glide.with(holder.itemView.getContext()).load(url).into(holder.ivVideoImage);
        holder.tvChannelName.setText(videoDetails.author());
        holder.tvVideoView.setText(formatViews(videoDetails.viewCount()));
        holder.tvVideoDuration.setText(convertTime(videoDetails.lengthSeconds()));

        holder.ivVideoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), HomeVideoActivity.class);
                intent.putExtra("videoTitle", videoDetails.title());
                intent.putExtra("videoId", videoDetails.videoId());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.ivVideoDownload.setOnClickListener((view) -> {
            Intent intent = new Intent(holder.itemView.getContext(), VideoFormatActivity.class);
            intent.putExtra("isId", true);
            intent.putExtra("videoId", videoDetails.videoId());
            holder.itemView.getContext().startActivity(intent);
        });

//        holder.ivVideoShare.setOnClickListener((view) -> {
//            Log.d(TAG, "onBindViewHolder:  ");
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setContinuation() {

        if (CheckInternet.checkInternet(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (result.hasContinuation()) {
                        RequestSearchContinuation nextRequest = new RequestSearchContinuation(result);
                        SearchResult continuation = downloader.searchContinuation(nextRequest).data();
                        Handler handler = new Handler(Looper.getMainLooper());
                        list.addAll(continuation.videos());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                        Log.d(TAG, "setContinuation: and size of list : " + list.size());
                    }
                }
            }).start();
        } else {
            Toast.makeText(context, "load filed", Toast.LENGTH_SHORT).show();
        }

    }

    private String convertTime(int ms) {
        String time;
        int x, seconds, minutes, hours;
        ms = ms * 1000;
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

    private String formatViews(long views) {
        if (views < 1000) {
            return views + " views";
        } else if (views < 1000000) {
            return String.format("%.1fK views", views / 1000.0);
        } else if (views < 1000000000) {
            return String.format("%.1fM views", views / 1000000.0);
        } else {
            return String.format("%.1fB views", views / 1000000000.0);
        }
    }

    public void shareLink() {
        String linkToShare = "https://www.example.com";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, linkToShare);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView ivVideoImage;
        TextView tvVideoTitle, tvChannelName;
        ImageView ivVideoShare, ivVideoDownload;
        TextView tvVideoDuration, tvVideoView;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);

            ivVideoImage = itemView.findViewById(R.id.iv_home_videoView);
            tvVideoTitle = itemView.findViewById(R.id.home_videoTitle);
            tvChannelName = itemView.findViewById(R.id.home_videoChannel);
            ivVideoDownload = itemView.findViewById(R.id.home_downloadBtn);
//            ivVideoShare = itemView.findViewById(R.id.home_share);
            tvVideoDuration = itemView.findViewById(R.id.tv_home_videoDuration);
            tvVideoView = itemView.findViewById(R.id.tv_home_videoView);
            context = itemView.getContext();
        }
    }
}
