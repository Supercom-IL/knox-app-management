package com.supercom.knox.appmanagement.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.supercom.knox.appmanagement.BuildConfig;
import com.supercom.knox.appmanagement.CameraReceiver;
import com.supercom.knox.appmanagement.FlightModeReceiver;
import com.supercom.knox.appmanagement.KnoxDeviceManager;
import com.supercom.knox.appmanagement.StateReceiver;
import com.supercom.knox.appmanagement.StatusManager;
import com.supercom.knox.appmanagement.reboot.RebootReceiver;

import timber.log.Timber;

public class App extends Application {
    private final static boolean ignoreUSBBlock = false;

    public static boolean isIgnoreUSBBlock() {
        return ignoreUSBBlock;
    }

    private static boolean registeredToIntents = false;


    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        AppService.start(getApplicationContext());
        registerToIntents(getApplicationContext());
        KnoxDeviceManager.setAllowPowerOffAndRestart(getApplicationContext(), false); // prevent the user from rebooting the device once the boot is completed
        KnoxDeviceManager.setCameraMode(getApplicationContext(), false);
        KnoxDeviceManager.setAirplaneMode(getApplicationContext(), false);
        KnoxDeviceManager.setAirplaneModeEnable(getApplicationContext(), false);

        StatusManager.getInstance(getApplicationContext()).loadCurrentStatus(true);
        StatusManager.getInstance(getApplicationContext()).activateLicenseIfRequired();
    }

    public void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static void registerToIntents(Context context) {
        if (registeredToIntents) return;

        registeredToIntents = true;

        context.registerReceiver(new RebootReceiver(), new IntentFilter("com.supercom.reboot"));
        context.registerReceiver(new CameraReceiver(), new IntentFilter("com.supercom.camera"));
        context.registerReceiver(new FlightModeReceiver(), new IntentFilter("com.supercom.fligh.mode"));
        context.registerReceiver(new StateReceiver(), new IntentFilter("com.supercom.knox.askState"));
    }
}
