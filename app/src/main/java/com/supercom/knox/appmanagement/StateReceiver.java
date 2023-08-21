package com.supercom.knox.appmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import timber.log.Timber;

public class StateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("StateReceiver onReceive()");

        Intent i = new Intent("com.supercom.knox.state");
        i.putExtra("state",StatusManager.getInstance().state.toJson());
        context.sendBroadcast(i);
    }
}
