package com.supercom.knox.appmanagement.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supercom.knox.appmanagement.KnoxDeviceManager;
import com.supercom.knox.appmanagement.application.App;

import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            KnoxDeviceManager.setCameraMode(context, false);
            KnoxDeviceManager.setAirplaneModeEnable(context, false);
            KnoxDeviceManager.setAllowPowerOffAndRestart(context, false); // prevent the user from rebooting the device once the boot is completed
        } catch (Exception e) {
            Timber.e("disable PowerOffAndRestart is failed: %s", e.getMessage());
        }

 App.registerToIntents(context);
    }
}

