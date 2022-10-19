package com.supercom.knox.appmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ActivateActivity extends AppCompatActivity implements StatusManager.StatusInterface {

    private final String TAG = "ActivateActivity";
    CheckBox tv_admin,tv_activate, tv_usb,  tv_mobile_data_roaming;
    TextView tv_log;
Button btn_activate,btn_deactivate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        StatusManager.getInstance(getApplicationContext());


        StatusManager.getInstance(getApplicationContext()).setListener(this);

        tv_admin=findViewById(R.id.tv_admin);
        tv_activate=findViewById(R.id.tv_activate);
        tv_usb=findViewById(R.id.tv_usb);
        tv_mobile_data_roaming=findViewById(R.id.tv_mobile_data_roaming);
        btn_activate=findViewById(R.id.btn_activate);
        btn_deactivate=findViewById(R.id.btn_deactivate);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.cb_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        activeApp();
        initUI();
    }

    private void activeApp() {
        if(!StatusManager.getInstance(getApplicationContext()).isAdminEnabled()){
            StatusManager.getInstance(getApplicationContext()).activateAdmin(ActivateActivity.this);
        }else if(!StatusManager.getInstance(getApplicationContext()).isActiveLicense()){
            StatusManager.getInstance(getApplicationContext()).activateLicense();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       StatusManager.getInstance(getApplicationContext()).onAddDeviceAdminActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onStatusChange() {
        initUI();
    }

    private void initUI() {
        StatusManager manager=StatusManager.getInstance(getApplicationContext());

        tv_log.clearComposingText();
        tv_log.setText("");

        for (int i=manager.messages.size()-1;i>=0;i--) {
            String m = manager.messages.get(i);
            tv_log.append(m);
            tv_log.append("\n");
        }

       btn_activate.setEnabled(!manager.isActiveLicense());
       btn_deactivate.setEnabled(false);//manager.isActiveLicense());

        tv_admin.setEnabled(manager.adminEnabled!= null);
        tv_activate.setEnabled(manager.activeLicense!= null);
        tv_usb.setEnabled(manager.disabledUSBPort != null);
        tv_mobile_data_roaming.setEnabled(manager.enabledMobileDataRoaming != null);

        tv_admin.setChecked(manager.isAdminEnabled());
        tv_activate.setChecked(manager.isActiveLicense());
        tv_usb.setChecked(manager.isUsbEnabled());
        tv_mobile_data_roaming.setChecked(manager.isDataRoamingEnabled());


        tv_activate.setEnabled(manager.activeLicense!= null);
        tv_usb.setEnabled(manager.disabledUSBPort != null);
        tv_mobile_data_roaming.setEnabled(manager.enabledMobileDataRoaming != null);
    }

    public void onActivateClick(View view) {
        activeApp();
    }

    public void onDectivateClick(View view) {

    }
}