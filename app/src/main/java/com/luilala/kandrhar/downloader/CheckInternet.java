package com.luilala.kandrhar.downloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class CheckInternet {
    public static boolean checkInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo wifiInfo = connectivityManager.getActiveNetworkInfo();
        if (mobileInfo != null && wifiInfo != null){
            if (mobileInfo.getState() == NetworkInfo.State.CONNECTED
            || wifiInfo.getState() == NetworkInfo.State.CONNECTED){
                return true;
            }
        }
        return false;

    }
}
