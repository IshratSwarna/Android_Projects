package com.example.passwordauthentication;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class BackgroundServices extends Service {
    // flag is used for stopping or running loop check of current app running.
    static int flag=0;
    static int flag2=0;
    public int counter = 0;
    //when any app is launched and it has password set on it that app name will be saved in current_app variable
    String current_app = "";


    Apply_password_on_AppDatabase db = new Apply_password_on_AppDatabase(this);
    //context is important. Every gui or activity or view have context.
    // I will use context of NotificationService class
    Context mContext;
    LockScreen obj = new LockScreen();
    private BroadcastReceiver receiver;
    private Timer timer;
    private TimerTask timerTask;


    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
        //add context or NotificationService to mContext variable;
        this.mContext = this;
        if(Build.VERSION.SDK_INT > 26)
            startMyOwnForeground();
        else
            startForeground(1,new Notification());
    }

    @RequiresApi(26)
    private void startMyOwnForeground()
    {
        String str = "example.permanence";

        NotificationChannel notificationChannel = new NotificationChannel(str, "Background Service", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setLightColor(-16776961);
        notificationChannel.setLockscreenVisibility(0);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        startForeground(2, new Notification.Builder(this, str).setOngoing(true).setContentTitle("App is running in background").setPriority(Notification.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_SERVICE).build());
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        System.out.println("Starting Timer!!!!!!!!!!\n");
        startTimer();
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        stoptimertask();
        Intent intent = new Intent();
        intent.setAction("restartservice");
        intent.setClass(this, RestartService.class);
        sendBroadcast(intent);
    }

    //set timer of one second repeat itself

    public void startTimer(){
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            public void run() {
            if (BackgroundServices.flag == 0) {
                Cursor allData = BackgroundServices.this.db.getAllData();
                ArrayList arrayList = new ArrayList();
                //arrayList contains the app package name that have been added for password protection
                while (allData.moveToNext()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("=========  ");
                    sb.append(BackgroundServices.this.printForegroundTask());
                    //Log.i("Count", sb.toString());
                    arrayList.add(allData.getString(0));
                }
                if (arrayList.contains(BackgroundServices.this.printForegroundTask())) {
                    BackgroundServices.flag = 1;
                    BackgroundServices backgroundServices = BackgroundServices.this;
                    backgroundServices.current_app = backgroundServices.printForegroundTask();
                    //when matches, Lockscreen gets the app package name
                    Intent intent = new Intent(BackgroundServices.this.mContext, LockScreen.class);
                    intent.putExtra("pack", BackgroundServices.this.printForegroundTask());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BackgroundServices.this.mContext.startActivity(intent);
                }
            }
            if (!BackgroundServices.this.printForegroundTask().equals("com.example.passwordauthentication") && BackgroundServices.flag2 == 0 && !BackgroundServices.this.printForegroundTask().equals(BackgroundServices.this.current_app)) {
                BackgroundServices.flag = 0;
            }
            if (BackgroundServices.this.printForegroundTask().equals("com.example.passwordauthentication")) {
                BackgroundServices.flag = 2;
            }
            }
        };
        this.timer.schedule(this.timerTask, 0, 100);
    }

    public void stoptimertask() {
        Timer timer2 = this.timer;
        if (timer2 != null) {
            timer2.cancel();
            this.timer = null;
        }
    }

    //get string of current app running
    public String printForegroundTask(){
        String str = "NULL";
        if (Build.VERSION.SDK_INT < 21) {
            return ((ActivityManager.RunningAppProcessInfo) ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().get(0)).processName;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(0, currentTimeMillis - 1000000, currentTimeMillis);
        if (queryUsageStats == null || queryUsageStats.size() <= 0) {
            System.out.println("str is"+ str + "\n");
            return str;
        }
        TreeMap treeMap = new TreeMap();
        for (UsageStats usageStats : queryUsageStats) {
            treeMap.put(Long.valueOf(usageStats.getLastTimeUsed()), usageStats);
        }
        if (!treeMap.isEmpty()) {
            return ((UsageStats) treeMap.get(treeMap.lastKey())).getPackageName();
        }
        System.out.println("str is"+ str + "\n");
        return str;
    }
}
