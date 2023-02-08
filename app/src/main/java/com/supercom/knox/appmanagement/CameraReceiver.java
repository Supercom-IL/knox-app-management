package com.supercom.knox.appmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import timber.log.Timber;

public class CameraReceiver extends BroadcastReceiver {
    static long lastReceiveTime;
    static boolean lastReceiveEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("CameraReceiver onReceive()");

        long receiveTime = new Date().getTime();
        boolean receiveEnabled = intent.getBooleanExtra("Enabled", false);
        int delayInSeconds = intent.getIntExtra("Delay", 0);
        if (Math.abs(lastReceiveTime - receiveTime) < 3000 && lastReceiveEnabled == receiveEnabled) {
            return;
        }

        lastReceiveEnabled = receiveEnabled;
        lastReceiveTime = receiveTime;

        if (delayInSeconds > 0) {
            runDelay(context, !receiveEnabled, delayInSeconds, receiveTime);
        }

        try {
            KnoxDeviceManager.setCameraMode(context, receiveEnabled);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void runDelay(Context context, boolean enabled, int delayInSeconds, long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TreadSleep(delayInSeconds * 1000);
                if (lastReceiveTime != time) {
                    return;
                }

                try {
                    KnoxDeviceManager.setCameraMode(context, enabled);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void TreadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
