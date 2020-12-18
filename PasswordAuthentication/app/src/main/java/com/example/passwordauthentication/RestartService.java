package com.example.passwordauthentication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartService extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Log.i("Broadcast Listened", "Service tried to stop");
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(new Intent(context, BackgroundServices.class));
            } else {
                context.startService(new Intent(context, BackgroundServices.class));
            }
        }
        //Log.i("Broadcast Listened", "Service tried to stop");
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, BackgroundServices.class));
        } else {
            context.startService(new Intent(context, BackgroundServices.class));
        }
    }
}
