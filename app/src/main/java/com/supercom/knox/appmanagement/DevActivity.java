package com.supercom.knox.appmanagement;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.supercom.knox.appmanagement.application.App;
import com.supercom.knox.appmanagement.application.AppService;

public class DevActivity extends AppCompatActivity implements StatusManager.StatusInterface {

    private final String TAG = "ActivateActivity";
    CheckBox tv_admin, tv_activate, tv_usb, tv_mobile_data_roaming, tv_camera, tv_flightMode;
    TextView tv_log;
    Button btn_activate, btn_deactivate, btn_reboot, btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_activate);
        StatusManager.getInstance(getApplicationContext());


        StatusManager.getInstance(getApplicationContext()).setListener(this);

        tv_admin = findViewById(R.id.tv_admin);
        btn_reboot = findViewById(R.id.btn_reboot);
        btn_close = findViewById(R.id.btn_close);
        tv_activate = findViewById(R.id.tv_activate);
        tv_usb = findViewById(R.id.tv_usb);
        tv_mobile_data_roaming = findViewById(R.id.tv_mobile_data_roaming);
        tv_camera = findViewById(R.id.tv_camera);
        tv_flightMode = findViewById(R.id.tv_flightMode);
        btn_activate = findViewById(R.id.btn_activate);
        btn_deactivate = findViewById(R.id.btn_deactivate);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.cb_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        initUI();
    }

    private void activeApp() {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());
        String keyText = "Activate by new license";

        if (!manager.isAdminEnabled()) {
            manager.activateAdmin(DevActivity.this);
            Toast.makeText(getApplicationContext(),"Set app admin first and " + keyText,Toast.LENGTH_SHORT ).show();
            return;
        }

        if(manager.activateLicense()){
            Toast.makeText(getApplicationContext(),keyText,Toast.LENGTH_SHORT ).show();
        }else{
            Toast.makeText(getApplicationContext(),manager.lastError,Toast.LENGTH_SHORT ).show();
        }

        manager.loadCurrentStatus(true);
        initUI();
    }

    private void deactiveApp() {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());
        String keyText = "Deactivate by new license";

        if(manager.deactivateLicense()){
            Toast.makeText(getApplicationContext(),keyText,Toast.LENGTH_SHORT ).show();
        }else{
            Toast.makeText(getApplicationContext(),manager.lastError,Toast.LENGTH_SHORT ).show();
        }

        manager.loadCurrentStatus(true);
        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        StatusManager.getInstance(getApplicationContext()).onAddDeviceAdminActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStatusChange() {
        initUI();
    }

    private void initUI() {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());

        tv_activate .setText("\tActivate Knox Licence");
        tv_log.clearComposingText();
        tv_log.setText("");

        for (int i = manager.messages.size() - 1; i >= 0; i--) {
            String m = manager.messages.get(i);
            tv_log.append(m);
            tv_log.append("\n");
        }

        if (App.isIgnoreUSBBlock() || BuildConfig.DEBUG) {
            tv_usb.setEnabled(false);
            tv_usb.setChecked(false);
            if (BuildConfig.DEBUG) {
                tv_usb.setText("\tEnabled USB for DEBUG mode");
            } else {
                tv_usb.setText("\tDisabled USB Plugin not required");
            }
            Drawable img = ContextCompat.getDrawable(this, R.drawable.activate_not_required);
            img.setBounds(0, 0, 60, 60);
            tv_usb.setCompoundDrawables(img, null, null, null);
        } else {
            tv_usb.setEnabled(manager.state.disabledUSBPort != null);
            tv_usb.setChecked(manager.isUsbEnabled());
        }

        tv_admin.setEnabled(manager.state.adminEnabled != null);
        tv_admin.setChecked(manager.isAdminEnabled());

        tv_activate.setEnabled(manager.state.activeLicense != null);
        tv_activate.setChecked(manager.isActiveLicense());

        tv_mobile_data_roaming.setEnabled(manager.state.enabledMobileDataRoaming != null);
        tv_mobile_data_roaming.setChecked(manager.isDataRoamingEnabled());

        tv_camera.setEnabled(manager.state.disabledCamera != null);
        tv_camera.setChecked(manager.isCameraDisabled());

        tv_flightMode.setEnabled(manager.state.disabledFlightMode != null);
        tv_flightMode.setChecked(manager.isFlightModeDisabled());
    }

    public void onActivateClick(View view) {
        activeApp();
    }

    public void onDeactivateClick(View view) {
        deactiveApp();
    }

    public void onCloseClick(View view) {
        finish();
    }

    public void onRebootClick(View view) {
        startActivity(new Intent(getApplicationContext(), RebootActivity.class));
    }

    public void onUSBClick(View view) {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());
        boolean usbMode = manager.isUsbDisable();
        manager.setUsbModes(getApplicationContext(), usbMode);
        Toast.makeText(this, "Usb " + (!usbMode ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        manager.loadCurrentStatus(true);
        initUI();
    }

     public void onFlightModeClick(View view) {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());
        boolean flightMode = !KnoxDeviceManager.isAirplaneModeEnabled(getApplicationContext());

        if(flightMode){
            if (!KnoxDeviceManager.setAirplaneModeEnable(getApplicationContext(), true)){
                Toast.makeText(this, KnoxDeviceManager.lastError, Toast.LENGTH_SHORT).show();
                return;
            }else{
                Toast.makeText(this, "Enable changing Flight Mode", Toast.LENGTH_SHORT).show();
            }
        }

        if (!KnoxDeviceManager.setAirplaneMode(getApplicationContext(), flightMode)){
            flightMode = false;
            Toast.makeText(this, KnoxDeviceManager.lastError, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Flight Mode " + (!flightMode ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        }

        if(!flightMode){
            if (!KnoxDeviceManager.setAirplaneModeEnable(getApplicationContext(), false)){
                Toast.makeText(this, KnoxDeviceManager.lastError, Toast.LENGTH_SHORT).show();
           }else {
                Toast.makeText(this, "Disable changing Flight Mode", Toast.LENGTH_SHORT).show();
            }
        }

        manager.loadCurrentStatus(true);
        initUI();
    }

    public void onCameraClick(View view) {
        StatusManager manager = StatusManager.getInstance(getApplicationContext());
        boolean cameraMode = manager.isCameraDisabled();
        KnoxDeviceManager.setCameraMode(getApplicationContext(), cameraMode);
        Toast.makeText(this, "Camera " + (cameraMode ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        manager.      loadCurrentStatus(true);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            StatusManager manager = StatusManager.getInstance(getApplicationContext());
            manager.loadCurrentStatus(false);
            initUI();
        }catch (Exception ex){

        }
        AppService.start(getApplicationContext());
    }
}