package com.supercom.knox.appmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supercom.knox.appmanagement.application.AppService;

import java.util.Date;

import timber.log.Timber;

public class TurnScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
         AppService.log(context, "Knox", "Turn Screen On",false);
         KnoxDeviceManager.turnScreenOn(context);
    }
}
