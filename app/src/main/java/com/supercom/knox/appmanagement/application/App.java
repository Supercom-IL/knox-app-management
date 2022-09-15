package com.supercom.knox.appmanagement.application;

import android.app.Application;
import android.content.IntentFilter;

import com.supercom.knox.appmanagement.BuildConfig;
import com.supercom.knox.appmanagement.reboot.RebootReceiver;

import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        getApplicationContext().registerReceiver(new RebootReceiver(),new IntentFilter("com.supercom.reboot"));
    }

    public void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
