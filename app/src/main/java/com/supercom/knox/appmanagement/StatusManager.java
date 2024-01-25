package com.supercom.knox.appmanagement;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.knox.license.ActivationInfo;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.supercom.knox.appmanagement.admin.AdminReceiver;
import com.supercom.knox.appmanagement.application.App;
import com.supercom.knox.appmanagement.util.Constants;

import java.util.ArrayList;

public class StatusManager {
    public static StatusManager _instance;
    public boolean useNewKey = true;
    public String lastError="";
    ArrayList<String> messages;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdmin;
    Context context;
    public DeviceState state = new DeviceState();
    public boolean enableDev;

    static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;

    public boolean isEnableDev() {
        return BuildConfig.DEBUG || enableDev;
    }

    public void switchLicenceToNewLicenceIfActivated() {
        if(state.activeLicense == null || !state.activeLicense){
            return;
        }

        if(isActivateByNewLicence()){
            return;
        }

        useNewKey = true;
        activateLicense();
    }

    public interface StatusInterface {
        void onStatusChange();
    }

    private StatusManager(){

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
        loadCurrentStatus(false);
    }

    public void loadCurrentStatus(boolean sendToServer) {
        if (devicePolicyManager.isAdminActive(deviceAdmin)) {
            state.adminEnabled = true;
        }

        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        try {
            if (licenseManager.getLicenseActivationInfo().getState() == ActivationInfo.State.ACTIVE) {
                state.activeLicense = true;
                state .isNewLicence = isActivateByNewLicence();
                switchLicenceToNewLicenceIfActivated();

                if(KnoxDeviceManager.isUsbDebuggingEnabled(context)!= null) {
                    state.disabledUSBPort = !KnoxDeviceManager.isUsbDebuggingEnabled(context);
                }
                state.enabledMobileDataRoaming = KnoxDeviceManager.isRoamingDataEnabled(context);
                state.disabledCamera = !KnoxDeviceManager.isCameraEnabled(context);
                state.disabledFlightMode = !KnoxDeviceManager.isAirplaneModeEnabled(context);
                state.flightMode = KnoxDeviceManager.getAirplaneMode(context);

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if(sendToServer){
            sendState(context);
        }
    }

    public void sendState(Context context){
        Intent i = new Intent("com.supercom.knox.state");
        state.updateByKnoxTime = System.currentTimeMillis();
        String json = StatusManager.getInstance(context).state.toJson();
        i.putExtra("state",json);
        context.sendBroadcast(i);
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
                KnoxDeviceManager.setAirplaneModeEnable(context,false);
                state.disabledFlightMode = !KnoxDeviceManager.isAirplaneModeEnabled(context);
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
        StatusManager.getInstance(context).loadCurrentStatus(true);
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

    public String getLicenseData() {
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);
        try {
            ActivationInfo info = licenseManager.getLicenseActivationInfo();
            String res = "key:"+info.getMaskedLicenseKey()+" state:"+info.getState();
            Log.i("YoadTest","LicenseData: " + res);
            return res;
        } catch (Exception e) {
            lastError = e.getMessage();
            Log.e("YoadTest", "getLicenseData Error: "+e.getMessage());
            return "";
        }
    }

    public Boolean isActivateByNewLicence() {
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);
        try {
            ActivationInfo info = licenseManager.getLicenseActivationInfo();
            String maskedLicenseKey = info.getMaskedLicenseKey();
            int len = maskedLicenseKey.length();
            String endKey = maskedLicenseKey.substring(len-5);
            len = Constants.KPE_LICENSE_KEY.length();
            String endNewKey = Constants.KPE_LICENSE_KEY.substring(len-5);
            return endKey.equals(endNewKey);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean activateLicense() {
        lastError="";
        String key = useNewKey ? Constants.KPE_LICENSE_KEY : Constants.KPE_LICENSE_KEY_OLD;
        String keyText = "Activate by " +(useNewKey ? "new" : "old")+" license";
        Log.i("YoadTest", keyText);

        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        // KPE License Activation TODO Add license key to Constants.java
        try {
            licenseManager.activateLicense(key);
            Log.i("YoadTest", "Activate success");
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            Log.e("YoadTest", "Activate Error: "+e.getMessage());
            return false;
        }
    }

    public boolean deactivateLicense() {
        lastError="";
        String key = useNewKey ? Constants.KPE_LICENSE_KEY : Constants.KPE_LICENSE_KEY_OLD;
        String keyText = "Deactivate by " +(useNewKey ? "new" : "old")+" license";
        Log.i("YoadTest", keyText);

        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(context);

        // KPE License Activation TODO Add license key to Constants.java
        try {
            licenseManager.deActivateLicense(key);
            Log.i("YoadTest", "Deactivate success");
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            Log.e("YoadTest", "Deactivate Error: "+e.getMessage());
            return false;
        }
    }
}
