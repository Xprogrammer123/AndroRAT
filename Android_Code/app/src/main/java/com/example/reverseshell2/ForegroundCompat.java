package com.example.reverseshell2;

import android.app.Notification;
import android.app.Service;
import android.content.pm.ServiceInfo;
import android.os.Build;

/**
 * Starts a foreground service with the correct type flags for Android 10 through 15+.
 */
public final class ForegroundCompat {

    private ForegroundCompat() {
    }

    public static void start(
            Service service,
            int notificationId,
            Notification notification,
            int foregroundServiceType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            service.startForeground(notificationId, notification, foregroundServiceType);
        } else {
            service.startForeground(notificationId, notification);
        }
    }

    public static int typeMicrophone() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
    }

    public static int typeCamera() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
    }

    public static int typeDataSync() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
    }
}
