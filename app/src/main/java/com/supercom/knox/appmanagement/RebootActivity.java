package com.supercom.knox.appmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.samsung.android.knox.EnterpriseDeviceManager;

public class RebootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            KnoxDeviceManager.setAllowPowerOffAndRestart(RebootActivity.this,true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int counter=0;
                while (counter<10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    counter++;

                    try {
                        EnterpriseDeviceManager.getInstance(RebootActivity.this).getPasswordPolicy().reboot("reboot device");
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}