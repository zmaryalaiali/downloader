package com.luilala.kandrhar.downloader.folder;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.widget.Toast;

import java.io.File;

public class Folder {
    private static File file ;

    private static void createFolder(Context context){
            file = Environment.getExternalStoragePublicDirectory("Downloader" );

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

}
