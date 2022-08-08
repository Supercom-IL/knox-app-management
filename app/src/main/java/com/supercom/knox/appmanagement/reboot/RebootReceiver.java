package com.supercom.knox.appmanagement.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.supercom.knox.appmanagement.KnoxDeviceManager;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RebootReceiver","onReceive");
        KnoxDeviceManager.reboot(context);
    }
}
