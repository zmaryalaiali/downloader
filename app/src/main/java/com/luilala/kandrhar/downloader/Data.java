package com.luilala.kandrhar.downloader;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.luilala.kandrhar.downloader.folder.Folder;
import com.luilala.kandrhar.downloader.model.DownloadModel;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import java.util.ArrayList;
import java.util.List;


public final class Data {

    public static  DownloadModel[] sampleUrls = new DownloadModel[]{};
    public static  DownloadModel[] sampleAudioUrls = new DownloadModel[]{};

    private Data() {

    }

    @NonNull
    private static List<Request> getFetchRequests(Context context) {
        final List<Request> requests = new ArrayList<>();
        for (DownloadModel sampleUrl : sampleUrls) {
            final Request request = new Request(sampleUrl.url, getFilePath(sampleUrl.type,sampleUrl.videoTitle, context));
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    public static List<Request> getFetchRequestWithGroupId(final int groupId, Context context) {
        final List<Request> requests = getFetchRequests(context);
        for (Request request : requests) {
            request.setGroupId(groupId);
        }
        return requests;
    }

    @NonNull
    public static String getFilePath(@NonNull final String type, String fileName,Context context) {

            final String dir = getSaveDir(context);
            return (dir + "/" + fileName);


    }

    @NonNull
    static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    @NonNull
    public static List<Request> getGameUpdates(Context context) {
        final List<Request> requests = new ArrayList<>();
        final String url = "http://speedtest.ftp.otenet.gr/files/test100k.db";
        for (int i = 0; i < 10; i++) {
            final String filePath = getSaveDir(context) + "/gameAssets/" + "asset_" + i + ".asset";
            final Request request = new Request(url, filePath);
            request.setPriority(Priority.HIGH);
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    public static String getSaveDir(Context context) {
        return Folder.getFile(context).getPath();
    }

    @NonNull
    public static String getSaveVideoDir(Context context) {
        return Folder.getVideoFIle(context).getPath();
    }
    public static String getSaveAudioDir(Context context) {
        return Folder.getAudioFile(context).getPath();
    }

}
