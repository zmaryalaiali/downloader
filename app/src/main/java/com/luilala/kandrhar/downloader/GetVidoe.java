package com.luilala.kandrhar.downloader;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetVidoe {
    @GET
    Call<VideoModel> getVideoInfo(@Url String url);
}
