package com.supercom.knox.appmanagement;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.samsung.android.knox.license.ActivationInfo;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.supercom.knox.appmanagement.admin.AdminReceiver;
import com.supercom.knox.appmanagement.application.App;
import com.supercom.knox.appmanagement.application.AppService;
import com.supercom.knox.appmanagement.util.Constants;

import java.util.ArrayList;

public class StatusManager {
    public static StatusManager _instance;
    ArrayList<String> messages;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdmin;
    Context context;
    public Boolean adminEnabled;
    public Boolean activeLicense;
    public Boolean disabledUSBPort;
    public Boolean enabledMobileDataRoaming;
    public Boolean disabledCamera;

    static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;

    public interface StatusInterface {
        void onStatusChange();
    }

    public static StatusManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new StatusManager(context);
        }
        return _instance;
    }

    public static StatusManager getInstance() {
        return _instance;
    }

    StatusInterface listener;

    public void setListener(StatusInterface listener) {
        this.listener = listener;
    }

    private StatusManager(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
        deviceAdmin = new ComponentName(context, AdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        loadCurrentStatus();
    }

    private void loadCurrentStatus() {
        if (devicePolicyManager.isAdminActive(deviceAdmin)) {
            adminEnabled = true;
        }
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        try {
            if (licenseManager.getLicenseActivationInfo().getState() == ActivationInfo.State.ACTIVE) {
                activeLicense = true;

                if(KnoxDeviceManager.isUsbDebuggingEnabled(context)!= null) {
                    disabledUSBPort = !KnoxDeviceManager.isUsbDebuggingEnabled(context);
                }
                enabledMobileDataRoaming = KnoxDeviceManager.isRoamingDataEnabled(context);

                disabledCamera = !KnoxDeviceManager.isCameraEnabled(context);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public boolean isAdminEnabled() {
        return adminEnabled != null && adminEnabled == true;
    }

    public boolean isAdminDisabled() {
        return adminEnabled != null && adminEnabled == false;
    }

    public boolean isActiveLicense() {
        return activeLicense != null && activeLicense == true;
    }

    public boolean isInactiveLicense() {
        return activeLicense != null && activeLicense == false;
    }

    public boolean isUsbEnabled() {
        return disabledUSBPort != null && disabledUSBPort == true;
    }

    public boolean isUsbDisable() {
        return disabledUSBPort != null && disabledUSBPort == false;
    }

    public boolean isCameraDisabled() {
        return disabledCamera != null && disabledCamera;
    }

    public boolean isDataRoamingEnabled() {
        return enabledMobileDataRoaming != null && enabledMobileDataRoaming == true;
    }

    public boolean isDataRoamingDisable() {
        return enabledMobileDataRoaming != null && enabledMobileDataRoaming == false;
    }

    public void setAdminEnabled(boolean enabled) {
        adminEnabled = enabled;

        callOnStatusChange();
        addMessage("Device admin " + (enabled ? "enabled" : "Disabled"));
    }

    public void setActiveLicense(boolean enabled, String message) {
        boolean currentStatus = (activeLicense == null || !activeLicense) && enabled;
        activeLicense = enabled;

        if (currentStatus) {
            try {
                if (!App.isIgnoreUSBBlock() && !BuildConfig.DEBUG) {
                    disableUsbModes(context);
                }
                callOnStatusChange();
                sleep(500);
                setMobileDataRoamingState(context);
                sleep(500);
                disabledCamera = !KnoxDeviceManager.setCameraMode(context,false);
                sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        addMessage(message);
        addMessage("Device admin " + (enabled ? "enabled" : "Disabled"));
        callOnStatusChange();
    }
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void addMessage(String message) {
        messages.add(message);
    }

    private void callOnStatusChange() {
        if (listener != null) {
            try {
                listener.onStatusChange();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enableMobileDataRoamingState() {
        this.enabledMobileDataRoaming = true;
        callOnStatusChange();
        addMessage("Set 'data roaming state' enable!");
    }

    public void disabledUsbPort() {
        this.disabledUSBPort = true;
        callOnStatusChange();
        addMessage("Usb Ports are disabled!");
    }

    private void disableUsbModes(Context context) {
        KnoxDeviceManager.setUsbPortModeDebugging(context, false);
        KnoxDeviceManager.setUsbPortModeMtp(context, false);
        KnoxDeviceManager.setUsbPortModeTethering(context, false);
        KnoxDeviceManager.setUsbPortModeHostStorage(context, false);
        StatusManager.getInstance(context).disabledUsbPort();
    }

    private void setMobileDataRoamingState(Context context) {
        KnoxDeviceManager.setMobileDataRoamingState(context, true);
        StatusManager.getInstance(context).enableMobileDataRoamingState();
    }

    /**
     * If Admin is deactivated, present a dialog to activate device administrator for this application.
     */
    public void activateAdmin(Activity activity) {
        boolean adminActive = devicePolicyManager.isAdminActive(deviceAdmin);
        if (adminActive) {
            return;
        }

        // Ask the user to add a new device administrator to the system
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
        // Start the add device admin activity
        activity.startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE);
    }

    public boolean onAddDeviceAdminActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE) {
            switch (resultCode) {
                // End user cancels the request
                case Activity.RESULT_CANCELED:
                    addMessage(context.getResources().getString(R.string.admin_cancelled));
                    return false;
                // End user accepts the request
                case Activity.RESULT_OK:
                    addMessage(context.getResources().getString(R.string.admin_activated));
                    activateLicense();
                    return true;
            }
        }

        return false;
    }

    public void activateLicense() {
        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        // KPE License Activation TODO Add license key to Constants.java
        licenseManager.activateLicense(Constants.KPE_LICENSE_KEY);
    }
}
