package com.supercom.knox.appmanagement.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.registerReceiver(new RebootReceiver(),new IntentFilter("com.supercom.reboot"));
    }
}

