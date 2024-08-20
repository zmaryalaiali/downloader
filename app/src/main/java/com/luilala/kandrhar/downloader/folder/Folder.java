package com.luilala.kandrhar.downloader.folder;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.luilala.kandrhar.downloader.R;

import java.io.File;

public class Folder {
    private static File file ;
    private static File videoFile;
    private static File audioFile;

    private static void createFolder(Context context){
            file = Environment.getExternalStoragePublicDirectory(context.getResources().getString(R.string.app_name) );

    }


    public static synchronized File getFile(Context context){
        createFolder(context);
        if (!file.exists()){
            if (file.mkdirs())
            Toast.makeText(context, "created successfully", Toast.LENGTH_SHORT).show();
            else
                file.mkdir();
        }
        else {
            Toast.makeText(context, "not created", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    public static synchronized File getVideoFIle(Context context){
        videoFile = context.getExternalFilesDir("videoFile");
        if (!videoFile.exists()){
            if (videoFile.mkdirs()){
                videoFile.mkdirs();
            }
        }
        return videoFile;
    }

    public static synchronized File getAudioFile(Context context){
        audioFile = context.getExternalFilesDir("audioFile");
        if (!audioFile.exists()){
            if (audioFile.mkdirs()){
                audioFile.mkdirs();
            }
        }
        return audioFile;
    }

}
