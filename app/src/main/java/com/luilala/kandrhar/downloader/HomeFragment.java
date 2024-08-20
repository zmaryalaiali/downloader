package com.luilala.kandrhar.downloader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.FormatField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.search.field.UploadDateField;
import com.luilala.kandrhar.downloader.adapter.AdapterHomeVideoRv;

import java.util.List;

public class HomeFragment extends Fragment {

    RequestSearchResult searchResult;

    RecyclerView rvVideo;
    AdapterHomeVideoRv adapterHomeVideoRv ;

    ShimmerFrameLayout shimmerFrameLayout;
    private boolean checkInternet ;

    CardView cardView;

    SearchView searchView;

    private static final String TAG = "MainActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvVideo = view.findViewById(R.id.home_rvVideos);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        cardView = view.findViewById(R.id.card_home);
        searchView = view.findViewById(R.id.home_search);

        cardView.setVisibility(View.GONE);
        rvVideo.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        searchView.clearFocus();
        Toast.makeText(getContext(), searchView.getQuery(), Toast.LENGTH_SHORT).show();

        checkInternet = CheckInternet.checkInternet(getContext());

        shimmerFrameLayout.startShimmer();

        rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));


        if (checkInternet){
            YoutubeDownloader downloader = new YoutubeDownloader();

            new Thread(() -> {

                searchResult = new RequestSearchResult("Afghanistan")
                        .filter(UploadDateField.MONTH
                                , FormatField.HD
                                , TypeField.VIDEO);

                SearchResult result = downloader.search(searchResult).data();

                List<SearchResultVideoDetails> videoDetails = result.videos();
                adapterHomeVideoRv = new AdapterHomeVideoRv(videoDetails , result , downloader);
                Log.d(TAG, "onViewCreated: list is fulling "+videoDetails.size());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: setAdapter done"+adapterHomeVideoRv.getItemCount());
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rvVideo.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        rvVideo.setAdapter(adapterHomeVideoRv);
                    }
                });

            }).start();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    cardView.setVisibility(View.GONE);
                    rvVideo.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);


                    checkInternet = CheckInternet.checkInternet(getContext());

                    shimmerFrameLayout.startShimmer();

                    rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));


                    if (checkInternet) {
                        YoutubeDownloader downloader = new YoutubeDownloader();

                        new Thread(() -> {

                            searchResult = new RequestSearchResult(query)
                                    .filter(UploadDateField.MONTH
                                            , FormatField.HD
                                            , TypeField.VIDEO);

                            SearchResult result = downloader.search(searchResult).data();

                            List<SearchResultVideoDetails> videoDetails = result.videos();
                            adapterHomeVideoRv = new AdapterHomeVideoRv(videoDetails, result, downloader);
                            Log.d(TAG, "onViewCreated: list is fulling " + videoDetails.size());
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d(TAG, "run: setAdapter done" + adapterHomeVideoRv.getItemCount());
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    rvVideo.setVisibility(View.VISIBLE);
                                    cardView.setVisibility(View.VISIBLE);
                                    searchView.setVisibility(View.VISIBLE);
                                    rvVideo.setAdapter(adapterHomeVideoRv);
                                }
                            });

                        }).start();
                    }
                    else {
                        Toast.makeText(getContext(), "no internet", Toast.LENGTH_SHORT).show();
                    }
                        return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        else {
            // no internet available
        }


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}