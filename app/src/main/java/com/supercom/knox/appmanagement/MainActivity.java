/**
 * DISCLAIMER: PLEASE TAKE NOTE THAT THE SAMPLE APPLICATION AND
 * SOURCE CODE DESCRIBED HEREIN IS PROVIDED FOR TESTING PURPOSES ONLY.
 * <p>
 * Samsung expressly disclaims any and all warranties of any kind,
 * whether express or implied, including but not limited to the implied warranties and conditions
 * of merchantability, fitness for com.samsung.knoxsdksample particular purpose and non-infringement.
 * Further, Samsung does not represent or warrant that any portion of the sample application and
 * source code is free of inaccuracies, errors, bugs or interruptions, or is reliable,
 * accurate, complete, or otherwise valid. The sample application and source code is provided
 * "as is" and "as available", without any warranty of any kind from Samsung.
 * <p>
 * Your use of the sample application and source code is at its own discretion and risk,
 * and licensee will be solely responsible for any damage that results from the use of the sample
 * application and source code including, but not limited to, any damage to your computer system or
 * platform. For the purpose of clarity, the sample code is licensed “as is” and
 * licenses bears the risk of using it.
 * <p>
 * Samsung shall not be liable for any direct, indirect or consequential damages or
 * costs of any type arising out of any action taken by you or others related to the sample application
 * and source code.
 */
package com.supercom.knox.appmanagement;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.supercom.knox.appmanagement.admin.AdminReceiver;
import com.supercom.knox.appmanagement.application.AppService;
import com.supercom.knox.appmanagement.util.Constants;
import com.supercom.knox.appmanagement.util.Utils;

import java.util.ArrayList;


/**
 * This activity displays the main UI of the application. This is a simple application to enable
 * and/or disable the use of certain apps using the Samsung Knox SDK.
 * Read more about the Knox SDK here:
 * https://docs.samsungknox.com/dev/knox-sdk/index.htm
 * <p>
 * Please insert valid KPE key to {@link Constants}.
 * </p>
 *
 * @author Samsung R&D Canada Technical Publications
 */
public class MainActivity extends AppCompatActivity implements StatusManager.StatusInterface {

    private final String TAG = "MainActivity";
    static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;
ArrayList<String> messages;
    private ComponentName deviceAdmin;
    private DevicePolicyManager devicePolicyManager;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //...called when the activity is starting. This is where most initialization should go.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages=new ArrayList<>();
        AppService.start(getApplicationContext());

        TextView logView = findViewById(R.id.logview_id);
        logView.setMovementMethod(new ScrollingMovementMethod());

        deviceAdmin = new ComponentName(MainActivity.this, AdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        StatusManager.getInstance(getApplicationContext()).adminEnabled=devicePolicyManager.isAdminActive(deviceAdmin);
        StatusManager.getInstance(getApplicationContext()).setListener(this);
        utils = new Utils(logView, TAG);

        activateAdmin();
    }

    /**
     * If Admin is deactivated, present a dialog to activate device administrator for this application.
     */
    private void activateAdmin() {
        boolean adminActive = devicePolicyManager.isAdminActive(deviceAdmin);

        if (adminActive) {
            utils.log(getResources().getString(R.string.admin_activated));
            utils.log("Usb Port Modes: Mtp,Debugging,Tethering,Host Storage are disabled!");
        } else {
            utils.log(getResources().getString(R.string.activating_admin));
            // Ask the user to add a new device administrator to the system
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
            // Start the add device admin activity
            startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE);
        }
    }

    /**
     * Handle result of device administrator activation request
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE) {
            switch (resultCode) {
                // End user cancels the request
                case Activity.RESULT_CANCELED:
                    utils.log(getResources().getString(R.string.admin_cancelled));
                    break;
                // End user accepts the request
                case Activity.RESULT_OK:
                    utils.log(getResources().getString(R.string.admin_activated));
                    activateLicense();
                    break;
            }
        }
    }

    /**
     * Activate a KPE license.
     *
     * Note that embedding your license key in code is unsafe and is done here for
     * demonstration purposes only.
     * Please visit https://docs.samsungknox.com/dev/common/knox-licenses.htm. for more details
     * about license keys.
     *
     */
    private void activateLicense() {
        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(this);

        // KPE License Activation TODO Add license key to Constants.java
        licenseManager.activateLicense(Constants.KPE_LICENSE_KEY);
        utils.log(getResources().getString(R.string.license_progress));
    }

    @Override
    public void onStatusChange() {
        initText();
    }

    private void initText() {
        TextView logview_id2 = findViewById(R.id.logview_id2);
        logview_id2.clearComposingText();
        logview_id2.setText("");

        if (StatusManager.getInstance(getApplicationContext()).adminEnabled==true) {
            logview_id2.append(getResources().getString(R.string.admin_activated));
        }else{
            logview_id2.append(getResources().getString(R.string.admin_inactivated));
        }

        if(StatusManager.getInstance(getApplicationContext()).disabledUSBPort ==true){
            logview_id2.append("Usb Port Modes: Mtp,Debugging,Tethering,Host Storage are disabled!");
        }else{
            logview_id2.append("Usb Port is enabled");
        }

        if(StatusManager.getInstance(getApplicationContext()).enabledMobileDataRoaming ==true){
            logview_id2.append("mobileDataRoaming is enabled");
        }else{
            logview_id2.append("mobileDataRoaming is disabled");
        }

        for (String m:messages){
            logview_id2.append(m);
        }
    }


/*    private void toggleUsbAccess() {
        EnterpriseKnoxManager ekm = EnterpriseKnoxManager.getInstance(this);
        try {

            // When you create container successfully,containerID will be returned via intent.
            // Use this containerID in below API.
            CreationParams params = new CreationParams();
            // Build creation params as per your needs.
            params.setConfigurationName("knox-b2b");
            // The key used by administrator in following API is mandatory to enable MDFPP(Mobile Device Fundamentals Protection Profile) SDP otherwise appropriate error code will be returned.
            //params.setPasswordResetToken("passwordResetToken");
            int initialRequestId = KnoxContainerManager.createContainer(params);
            KnoxContainerManager kcm = ekm.getKnoxContainerManager(initialRequestId);
            ContainerConfigurationPolicy ccp = kcm.getContainerConfigurationPolicy();
            boolean status = ccp.enableUsbAccess(true, null);
            isUsbEnabled = !isUsbEnabled;
            Toast.makeText(this, "Usb" + (isUsbEnabled ? "enabled" : "disabled" + " status: " + status), Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
        }
    }*/
}