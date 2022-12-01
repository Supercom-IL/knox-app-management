package com.supercom.knox.appmanagement.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supercom.knox.appmanagement.KnoxDeviceManager;

import timber.log.Timber;

public class RebootReceiver extends BroadcastReceiver {

    //TODO: receiver which receive reboot commands from other Supercom's app - this app should transform to SDK instead of app!!!
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("RebootReceiver onReceive()");

        try {
            KnoxDeviceManager.reboot(context);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
