package com.luilala.kandrhar.downloader;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tonyodev.fetch2.FetchIntent;
import com.tonyodev.fetch2.util.NotificationUtilsKt;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Retry", Toast.LENGTH_SHORT).show();
//       NotificationUtilsKt. onDownloadNotificationActionTriggered(context, intent, this);

        String action = intent.getStringExtra(FetchIntent.EXTRA_ACTION_TYPE);
    if (action.equals("5")){
        Toast.makeText(context, "Retry", Toast.LENGTH_SHORT).show();
    }

    }
}