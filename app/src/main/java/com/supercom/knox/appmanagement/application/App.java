package com.supercom.knox.appmanagement.application;

import android.app.Application;
import android.content.IntentFilter;

import com.supercom.knox.appmanagement.BuildConfig;
import com.supercom.knox.appmanagement.CameraReceiver;
import com.supercom.knox.appmanagement.KnoxDeviceManager;
import com.supercom.knox.appmanagement.reboot.RebootReceiver;

import timber.log.Timber;

public class App extends Application {
private final static boolean ignoreUSBBlock=false;
    public static boolean isIgnoreUSBBlock() {
        return ignoreUSBBlock;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        AppService.start(getApplicationContext());
        getApplicationContext().registerReceiver(new RebootReceiver(),new IntentFilter("com.supercom.reboot"));
        getApplicationContext().registerReceiver(new CameraReceiver(),new IntentFilter("com.supercom.camera"));
        KnoxDeviceManager.setAllowPowerOffAndRestart(getApplicationContext(), false); // prevent the user from rebooting the device once the boot is completed
        KnoxDeviceManager.setCameraMode(getApplicationContext(),false);
    }

    public void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
