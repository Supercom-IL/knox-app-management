package com.supercom.knox.appmanagement;

public class DeviceState {
    public Boolean adminEnabled;
    public Boolean activeLicense;
    public Boolean disabledUSBPort;
    public Boolean enabledMobileDataRoaming;
    public Boolean disabledCamera;

    public String toJson(){
        return "{" +
                "\"adminEnabled\":" +adminEnabled+","+
                "\"activeLicense\":" +activeLicense+","+
                "\"disabledUSBPort\":" +disabledUSBPort+","+
                "\"enabledMobileDataRoaming\":" +enabledMobileDataRoaming+","+
                "\"disabledCamera\":" +disabledCamera+
                "}";
    }
}
