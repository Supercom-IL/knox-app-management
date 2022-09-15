package com.supercom.knox.appmanagement.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supercom.knox.appmanagement.KnoxDeviceManager;

import timber.log.Timber;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("RebootReceiver onReceive()");
        KnoxDeviceManager.reboot(context);
    }
}
