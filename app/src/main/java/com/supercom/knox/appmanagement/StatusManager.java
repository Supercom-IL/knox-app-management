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
import com.supercom.knox.appmanagement.util.Constants;

import java.util.ArrayList;

public class StatusManager {
    public static StatusManager _instance;
    ArrayList<String> messages;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdmin;
    Context context;
    public DeviceState state = new DeviceState();

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
            state.adminEnabled = true;
        }
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        try {
            if (licenseManager.getLicenseActivationInfo().getState() == ActivationInfo.State.ACTIVE) {
                state.activeLicense = true;

                if(KnoxDeviceManager.isUsbDebuggingEnabled(context)!= null) {
                    state.disabledUSBPort = !KnoxDeviceManager.isUsbDebuggingEnabled(context);
                }
                state.enabledMobileDataRoaming = KnoxDeviceManager.isRoamingDataEnabled(context);
                state.disabledCamera = !KnoxDeviceManager.isCameraEnabled(context);

                state.disabledFlightMode = !KnoxDeviceManager.isAirplaneModeEnabled(context);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public boolean isAdminEnabled() {
        return  state.adminEnabled != null &&  state.adminEnabled == true;
    }

    public boolean isAdminDisabled() {
        return  state.adminEnabled != null &&  state.adminEnabled == false;
    }

    public boolean isActiveLicense() {
        return  state.activeLicense != null &&  state.activeLicense == true;
    }

    public boolean isInactiveLicense() {
        return  state.activeLicense != null &&  state.activeLicense == false;
    }

    public boolean isUsbEnabled() {
        return  state.disabledUSBPort != null &&  state.disabledUSBPort == true;
    }

    public boolean isUsbDisable() {
        return  state.disabledUSBPort != null &&  state.disabledUSBPort == false;
    }

    public boolean isCameraDisabled() {
        return  state.disabledCamera != null &&  state.disabledCamera;
    }
    public boolean isFlightModeDisabled() {
        return  state.disabledFlightMode != null &&  state.disabledFlightMode;
    }
    public boolean isDataRoamingEnabled() {
        return  state.enabledMobileDataRoaming != null &&  state.enabledMobileDataRoaming == true;
    }

    public boolean isDataRoamingDisable() {
        return  state.enabledMobileDataRoaming != null &&  state.enabledMobileDataRoaming == false;
    }

    public void setAdminEnabled(boolean enabled) {
        state.adminEnabled = enabled;

        callOnStatusChange();
        addMessage("Device admin " + (enabled ? "enabled" : "Disabled"));
    }

    public void setActiveLicense(boolean enabled, String message) {
        boolean currentStatus = ( state.activeLicense == null || ! state.activeLicense) && enabled;
        state.activeLicense = enabled;

        if (currentStatus) {

            try {
                if (!App.isIgnoreUSBBlock() && !BuildConfig.DEBUG) {
                    setUsbModes(context,false);
                }
                callOnStatusChange();
                sleep(500);
                setMobileDataRoamingState(context);
                sleep(500);
                state.disabledCamera = !KnoxDeviceManager.setCameraMode(context,false);
                sleep(500);
                state.disabledFlightMode = !KnoxDeviceManager.setAirplaneModeEnable(context,false);
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
        state.enabledMobileDataRoaming = true;
        callOnStatusChange();
        addMessage("Set 'data roaming state' enable!");
    }

    public void disabledUsbPort() {
        setDisabledUsbPort(true);
    }
    public void setDisabledUsbPort(boolean disabled) {
        state.disabledUSBPort = disabled;
        callOnStatusChange();
        addMessage("Usb Ports are "+ ((disabled) ? "disabled" : "enable")+"!");
    }

    public void setUsbModes(Context context,boolean mode) {
        KnoxDeviceManager.setUsbPortModeDebugging(context, mode);
        KnoxDeviceManager.setUsbPortModeMtp(context, mode);
        KnoxDeviceManager.setUsbPortModeTethering(context, mode);
        KnoxDeviceManager.setUsbPortModeHostStorage(context, mode);
        setDisabledUsbPort(!mode);
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
