package com.supercom.knox.appmanagement;

public class DeviceState {
    public Boolean adminEnabled;
    public Boolean activeLicense;
    public Boolean disabledUSBPort;
    public Boolean enabledMobileDataRoaming;
    public Boolean disabledCamera;
    public Boolean disabledFlightMode;

    public String toJson(){
        return "{" +
                "\"adminEnabled\":" +isTrue(adminEnabled) +","+
                "\"activeLicense\":" +isTrue(activeLicense)+","+
                "\"disabledUSBPort\":" +isTrue(disabledUSBPort)+","+
                "\"enabledMobileDataRoaming\":" +isTrue(enabledMobileDataRoaming)+","+
                "\"disabledCamera\":" +isTrue(disabledCamera)+","+
                "\"disabledFlightMode\":" +isTrue(disabledFlightMode)+
                "}";
    }

    private boolean isTrue(Boolean b){
        return b != null && b;
    }
}
