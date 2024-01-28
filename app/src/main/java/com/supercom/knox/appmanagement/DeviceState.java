package com.supercom.knox.appmanagement;

public class DeviceState {
    private static String appVersion = BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")";

    public long updateByKnoxTime;
    public Boolean adminEnabled;
    public Boolean activeLicense;
    public Boolean isNewLicence;
    public Boolean disabledUSBPort;
    public Boolean enabledMobileDataRoaming;
    public Boolean disabledCamera;
    public Boolean disabledFlightMode;
    public Boolean flightMode;

    public String toJson(){
        return "{" +
                "\"appVersion\":" +appVersion +","+
                "\"updateByKnoxTime\":" +updateByKnoxTime +","+
                "\"adminEnabled\":" +isTrue(adminEnabled) +","+
                "\"activeLicense\":" +isTrue(activeLicense)+","+
                "\"isNewLicence\":" +isTrue(isNewLicence)+","+
                "\"disabledUSBPort\":" +isTrue(disabledUSBPort)+","+
                "\"enabledMobileDataRoaming\":" +isTrue(enabledMobileDataRoaming)+","+
                "\"disabledCamera\":" +isTrue(disabledCamera)+","+
                "\"disabledFlightMode\":" +isTrue(disabledFlightMode)+","+
                "\"flightMode\":" +isTrue(flightMode)+
                "}";
    }

    private boolean isTrue(Boolean b){
        return b != null && b;
    }
}
