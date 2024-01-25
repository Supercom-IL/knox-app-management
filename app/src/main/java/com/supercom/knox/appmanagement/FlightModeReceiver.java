package com.supercom.knox.appmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supercom.knox.appmanagement.application.AppService;

import java.util.Date;

import timber.log.Timber;

public class FlightModeReceiver extends BroadcastReceiver {
    static long lastReceiveTime;
    static boolean lastReceiveEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("FlightModeReceiver onReceive()");

        long receiveTime = new Date().getTime();
        boolean receiveEnabled = intent.getBooleanExtra("Enabled", false);
        int delayInSeconds = intent.getIntExtra("Delay", 0);
        AppService.log(context, "Knox", "FlightModeReceiver onReceive : Enabled:"+receiveEnabled+", Delay:"+delayInSeconds,false);
        if (delayInSeconds > 0 && Math.abs(lastReceiveTime - receiveTime) < 3000 && lastReceiveEnabled == receiveEnabled) {
            return;
        }

        lastReceiveEnabled = receiveEnabled;
        lastReceiveTime = receiveTime;

        if (delayInSeconds > 0) {
            runDelay(context, !receiveEnabled, delayInSeconds, receiveTime);
        }

        doRequest(context,receiveEnabled);
    }

    private void doRequest(Context context,boolean enabled) {
        try {
             if (enabled) {
                KnoxDeviceManager.setAirplaneModeEnable(context, true);
                KnoxDeviceManager.setAirplaneMode(context,true);
            } else {
                KnoxDeviceManager.setAirplaneMode(context,false);
                KnoxDeviceManager.setAirplaneModeEnable(context, false);
            }
            StatusManager.getInstance(context).loadCurrentStatus(true);
        } catch (SecurityException e) {
            AppService.log(context, "Knox", "FlightModeReceiver doRequest ERROR: "+e.getMessage(), true);
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

                doRequest(context,enabled);
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
