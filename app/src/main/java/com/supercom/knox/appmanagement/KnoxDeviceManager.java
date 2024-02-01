package com.supercom.knox.appmanagement;

import android.content.Context;
import android.util.Log;

import com.samsung.android.knox.EnterpriseDeviceManager;

import com.samsung.android.knox.custom.CustomDeviceManager;
import com.samsung.android.knox.custom.SettingsManager;
import com.supercom.knox.appmanagement.application.AppService;

public class KnoxDeviceManager {
    public static String lastError = "";
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

    public static Boolean isRoamingDataEnabled(Context context) {
        try {
            return EnterpriseDeviceManager.getInstance(context).getRoamingPolicy().isRoamingDataEnabled();
        } catch (Exception ex) {
            return null;
        }
    }

    public static void startApp(Context context, String packageName, String className) {
        EnterpriseDeviceManager.getInstance(context).getApplicationPolicy().startApp(packageName, className);
    }

    public static boolean stopApp(Context context, String packageName) {
        return EnterpriseDeviceManager.getInstance(context).getApplicationPolicy().stopApp(packageName);
    }

    /*
     * enable or disable Camera access
     * standard sdk
     */
    public static boolean setCameraMode(Context context, boolean isEnabled) {
        String message = isEnabled ? "Knox allows access to the camera" : "Knox Prevents use of camera";
        try {
            EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setCameraState(isEnabled);
            boolean res = EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isCameraEnabled(false);
            AppService.log(context, "Camera", message, false);
            return res;
        } catch (Exception e) {
            AppService.log(context, "Camera", message+" ERROR: "+e.getMessage(), true);
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

    public static boolean canChangeCameraMode(Context context) {
        try {
            boolean res = EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isCameraEnabled(false);
            res = EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().setCameraState(res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean canSetAirplaneMode(Context context) {
        try {
            setAirplaneModeEnable(context, true);

            CustomDeviceManager cdm = CustomDeviceManager.getInstance();
            SettingsManager kcsm = cdm.getSettingsManager();
            kcsm.setFlightModeState(CustomDeviceManager.OFF);
            return true;
        } catch (Exception e) {

        } finally {
            setAirplaneModeEnable(context, false);
        }

        return false;
    }

    /*
     * enable or disable AirplaneMode access
     * standard sdk
     */
    public static boolean setAirplaneModeEnable(Context context, boolean isEnabled) {
        Log.i("YoadTest", "Set Airplane mode enable to " + isEnabled);
        String message = isEnabled ? "Knox allows access and change Airplane Mode" : "Knox Prevents to set on Airplane Mode";
        try {
            boolean res =  EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().allowAirplaneMode(isEnabled);
            AppService.log(context, "Knox", message+ " result:"+res, false);
            return res;
        } catch (Exception e) {
            lastError = e.getMessage();
            AppService.log(context, "Knox", message+" ERROR: "+e.getMessage(), true);
            e.printStackTrace();
            Log.e("YoadTest","setAirplaneModeEnable ERROR: "+ e.getMessage());
            return false;
        }
     }

    public static boolean isAirplaneModeEnabled(Context context) {
        try {
            return EnterpriseDeviceManager.getInstance(context).getRestrictionPolicy().isAirplaneModeAllowed();
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            Log.e("YoadTest","isAirplaneModeEnabled ERROR: "+ e.getMessage());
        }
        return true;
    }

    public static boolean setAirplaneMode(Context context,boolean isEnabled) {
        Log.i("YoadTest", "setAirplaneMode(" + isEnabled+")");
        try {
            CustomDeviceManager cdm = CustomDeviceManager.getInstance();
            SettingsManager kcsm = cdm.getSettingsManager();
            int res = kcsm.setFlightModeState(isEnabled ? CustomDeviceManager.ON : CustomDeviceManager.OFF);
            String message =  "Set Airplane Mode to " + getOnOffText(isEnabled) + " result:"+res;
            AppService.log(context, "Knox", message, false);
            Log.i("YoadTest", "res:" + res);
            return true;
        } catch(SecurityException e) {
            lastError = e.getMessage();
            AppService.log(context, "Knox", "Set Airplane Mode to "+getOnOffText(isEnabled)+" ERROR: "+e.getMessage(), true);
            e.printStackTrace();
            Log.e("YoadTest","setAirplaneMode ERROR: "+ e.getMessage());
            return  false;
        }
    }

    public static boolean getAirplaneMode(Context context) {
        Log.i("YoadTest", "getAirplaneMode");
        try {
            int res = android.provider.Settings.Global.getInt(
                    context.getContentResolver(),
                    android.provider.Settings.Global.AIRPLANE_MODE_ON, 0);
            Log.i("YoadTest", "res:" + res);
            return res != 0;
        } catch (SecurityException e) {
            AppService.log(context, "Knox", "getAirplaneMode ERROR: " + e.getMessage(), true);
            e.printStackTrace();
            Log.e("YoadTest", "getAirplaneMode ERROR: " + e.getMessage());
            return false;
        }
    }

    private static String getOnOffText(boolean enabled){
        return enabled ? "ON" : "OFF";
    }
}
