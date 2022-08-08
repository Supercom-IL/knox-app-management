package com.supercom.knox.appmanagement.application;

import android.app.Application;
import android.content.IntentFilter;

import com.supercom.knox.appmanagement.reboot.RebootReceiver;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext().registerReceiver(new RebootReceiver(),new IntentFilter("com.supercom.reboot"));
    }
}
