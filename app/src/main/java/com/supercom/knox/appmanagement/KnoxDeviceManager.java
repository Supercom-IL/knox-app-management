package com.supercom.knox.appmanagement;

import android.content.Context;

import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.restriction.RoamingPolicy;

public class KnoxDeviceManager {
    public static void setUsbPortModeMtp(Context context, boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setUsbMediaPlayerAvailability(isEnabled);
        //utils.log("Usb Port Mode Mtp is: " + (isEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public static void setUsbPortModeDebugging(Context context, boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setUsbDebuggingEnabled(isEnabled);
        //utils.log("Usb Port Mode Debugging is: " + (isEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public static void setUsbPortModeTethering(Context context, boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setUsbTethering(isEnabled);
        //utils.log("Usb Port Mode Tethering is: " + (isEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public static void setUsbPortModeHostStorage(Context context, boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().allowUsbHostStorage(isEnabled);
        //utils.log("Usb Port Mode Host Storage is: " + (isEnabled ? "enabled" : "disabled"));
    }

    /*
     * allow or disallow the user to power off the device by pressing the power button.
     * standard sdk
     */
    public static void setAllowPowerOffAndRestart(Context context, boolean isEnabled) throws SecurityException {
        EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().allowPowerOff(isEnabled);
    }

    public static void reboot(Context context) throws SecurityException {
        setAllowPowerOffAndRestart(context, true);
        EnterpriseDeviceManager.getInstance(context).getPasswordPolicy().reboot("reboot device");
    }

    public static boolean setMobileDataRoamingState(Context context,boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RoamingPolicy roamingPolicy = edm.getRoamingPolicy();
        try {
            roamingPolicy.setRoamingData(state);
            return true;
        }catch(SecurityException e) {
            return false;
        }
    }
}
