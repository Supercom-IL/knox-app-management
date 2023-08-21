package com.supercom.knox.appmanagement;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.samsung.android.knox.EnterpriseDeviceManager;


import com.supercom.knox.appmanagement.application.AppService;

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

    public static Boolean isUsbDebuggingEnabled(Context context) {
        try {
            return EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isUsbDebuggingEnabled();
        } catch (Exception ex) {
            return null;
        }
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

    /*1
     * allow or disallow the user to power off the device by pressing the power button.
     * standard sdk
     */
    public static void setAllowPowerOffAndRestart(Context context, boolean isEnabled) throws SecurityException {
        //EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().allowPowerOff(isEnabled);
    }

    public static void reboot(Context context) throws SecurityException {
        setAllowPowerOffAndRestart(context, true);
        EnterpriseDeviceManager.getInstance(context).getPasswordPolicy().reboot("reboot device");
    }

    /*
     * allow or disallow the user to toggle mobile data roaming.
     * standard sdk
     */
    public static void setMobileDataRoamingState(Context context, boolean isEnabled) throws SecurityException {
        EnterpriseDeviceManager.getInstance(context).getRoamingPolicy().setRoamingData(isEnabled);
    }
    public static Boolean isRoamingDataEnabled(Context context)  {
        try {
            return EnterpriseDeviceManager.getInstance(context).getRoamingPolicy().isRoamingDataEnabled();
        }catch (Exception ex){
            return null;
        }
    }

    public static void startApp(Context context,String packageName,String className) {
        EnterpriseDeviceManager.getInstance(context).getApplicationPolicy().startApp(packageName,className);
    }

    public static boolean stopApp(Context context,String packageName) {
       return EnterpriseDeviceManager.getInstance(context).getApplicationPolicy().stopApp(packageName);
    }

    /*
     * enable or disable Camera access
     * standard sdk
     */
    public static boolean setCameraMode(Context context, boolean isEnabled) {
        try {
            EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setCameraState(isEnabled);
            boolean res = EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isCameraEnabled(false);
            String message = res ? "Knox allows access to the camera" : "Knox Prevents use of camera";
            AppService.log(context,"Camera",message,false);
            return res;
        } catch (Exception e) {
            AppService.log(context,"Camera",e.getMessage(),true);
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isCameraEnabled(Context context) {
        try {
           return EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isCameraEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
