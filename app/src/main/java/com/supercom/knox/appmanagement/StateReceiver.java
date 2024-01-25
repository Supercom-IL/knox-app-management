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
        StatusManager.getInstance(context).enableDev = intent.getBooleanExtra("EnableDevMode",false);
        StatusManager.getInstance(context).loadCurrentStatus(true);
    }
}
