package com.supercom.knox.appmanagement.application;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.supercom.knox.appmanagement.KnoxDeviceManager;
import com.supercom.knox.appmanagement.R;

import java.util.Date;
import java.util.Hashtable;

public class AppService extends Service {
    String KeepRunningIntentAction="com.supercom.knox.appmanagement.application.AppService.KeepRunning";
    String RunningIntentAction="com.supercom.knox.appmanagement.application.AppService.Running";
    static boolean isRunning;
    BroadcastReceiver appListener;
    Hashtable<String,AppData> nextKeepRunningAppIntent;
String nextKeepRunningIntentObject="nextKeepRunningIntentObject";

    public AppService() {
        nextKeepRunningAppIntent=new Hashtable<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isRunning = true;
        startForeground(99911, getNotificationChannel(getApplicationContext()));

        listenToKeepRunningIntents();
        runThread();
    }

    private void listenToKeepRunningIntents() {
        appListener=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("KeepRunning","KA onReceive");
                if(intent.getAction().equals(KeepRunningIntentAction)){
                    AppData app=new AppData(intent);
                    if (!app.isValidData()){
                        return;
                    }

                    Log.i("KeepRunning","KA onReceive AppData"+" packageName:"+app.packageName+" className:"+app.className+" nextKeep:"+app.nextKeep);
                    synchronized (nextKeepRunningIntentObject) {
                        if (nextKeepRunningAppIntent.contains(app.packageName)) {
                            nextKeepRunningAppIntent.remove(app.packageName);
                        }

                        nextKeepRunningAppIntent.put(app.packageName, app);
                    }
                }
            }
        };

        IntentFilter filter=new IntentFilter("");

        filter.addAction(KeepRunningIntentAction);

        registerReceiver(appListener,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appListener);
    }

    private void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Log.i("KeepRunning","KA check status ");
                    sleep(60000);
                    sendBroadcast(new Intent(RunningIntentAction));
                    synchronized (nextKeepRunningIntentObject) {
                        for (AppData app : nextKeepRunningAppIntent.values()){
                            if(app.nextKeep < new Date().getTime()){
                                restartApp(app);
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void restartApp(AppData app) {
        boolean b = KnoxDeviceManager.stopApp(getApplicationContext(), app.packageName);
        Log.i("KeepRunning","stopApp "+b);
        sleep(5000);
        Log.i("KeepRunning","startApp");
        KnoxDeviceManager.startApp(getApplicationContext(),app.packageName,app.className);
    }

    class AppData {
        AppData(Intent intent) {
            try {
                className = intent.getStringExtra("className");
                packageName = intent.getStringExtra("packageName");
                nextKeep = intent.getLongExtra("nextKeep", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String className;
        String packageName;
        Long nextKeep;
        public boolean isValidData() {
            return(packageName!=null && packageName.length() > 0 || nextKeep > 0) ;
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification getNotificationChannel(Context context) {
        NotificationManager mNotific = null;
        CharSequence name = "Ragav";
        String desc = "this is notific";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        final String ChannelID = "my_channel_03";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotific = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(ChannelID, name,
                    importance);
            mChannel.setDescription(desc);
            mChannel.setLightColor(Color.CYAN);
            mChannel.canShowBadge();
            mChannel.setShowBadge(true);
            mNotific.createNotificationChannel(mChannel);
        }

        String Body = "App knox manager";

        Notification n = new Notification.Builder(context, ChannelID)
                .setContentTitle("Knox service")
                .setContentText(Body)
                .setBadgeIconType(R.drawable.ic_baseline_memory_240)
                .setNumber(5)
                .setSmallIcon(R.drawable.ic_baseline_memory_24)
                .setAutoCancel(true)
                .build();

        return n;
    }

    public static void start(Context context){
        if(isRunning){
            return;
        }

        isRunning=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, AppService.class));
        } else {
            context.startService(new Intent(context, AppService.class));
        }
    }
}