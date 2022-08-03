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
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.samsung.android.knox.AppIdentity;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.application.ApplicationPolicy;
import com.samsung.android.knox.license.EnterpriseLicenseManager;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;

import java.util.ArrayList;
import java.util.List;



/**
 * This activity displays the main UI of the application. This is a simple application to enable
 * and/or disable the use of certain apps using the Samsung Knox SDK.
 * Read more about the Knox SDK here:
 * https://docs.samsungknox.com/dev/knox-sdk/index.htm
 * <p>
 * Please insert valid KPE key to {@link }.
 * </p>
 *
 * @author Samsung R&D Canada Technical Publications
 */
public class MainActivity2 extends AppCompatActivity {

    private final String TAG = "MainActivity";
    static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;

    private Button mToggleAdminBtn;
    private ComponentName mDeviceAdmin;
    private DevicePolicyManager mDevicePolicyManager;
    private Utils mUtils;

    private boolean isUsbEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //...called when the activity is starting. This is where most initialization should go.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

        TextView logView = findViewById(R.id.logview_id);
        logView.setMovementMethod(new ScrollingMovementMethod());
        mToggleAdminBtn = findViewById(R.id.toggleAdminBtn);
        Button activateLicenseBtn = findViewById(R.id.activateLicenseBtn);
        Button deactivateLicenseBtn = findViewById(R.id.deactivateLicenseBtn);
        Button showEnabledPackagesBtn = findViewById(R.id.showEnabledPackagesBtn);
        Button showDisabledPackagesBtn = findViewById(R.id.showDisabledPackagesBtn);
        Button enableAllDisabledPackagesBtn = findViewById(R.id.enableAllDisabledPackagesBtn);
        Button enablePackageBtn = findViewById(R.id.enablePackageBtn);
        Button disablePackageBtn = findViewById(R.id.disablePackageBtn);
        Button activateBackwardsCompatibleKeyBtn = findViewById(R.id.activateBackwardsCompatibleKeyBtn);
        Button toggleBatteryOptimizationWhitelistBtn = findViewById(R.id.toggleBatteryOptimizationWhitelistBtn);
        Button toggleForceStopBlacklistBtn = findViewById(R.id.toggleForceStopBlacklistBtn);
        Button toggleUsbAccessBtn = findViewById(R.id.toggleUsbAccessBtn);

        mDeviceAdmin = new ComponentName(MainActivity2.this, AdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mUtils = new Utils(logView, TAG);

        mToggleAdminBtn.setOnClickListener(v -> toggleAdmin());
        activateLicenseBtn.setOnClickListener(v -> activateLicense());
        deactivateLicenseBtn.setOnClickListener(v -> deactivateLicense());
        activateBackwardsCompatibleKeyBtn.setOnClickListener(v -> activateBackwardsCompatibleKey());


        toggleUsbAccessBtn.setOnClickListener(v -> {
           setUsbPortModeDebugging(isUsbEnabled);
            setUsbPortModeMtp(isUsbEnabled);
            setUsbPortModeTethering(isUsbEnabled);
            setUsbPortModeHostStorage(isUsbEnabled);
            isUsbEnabled = !isUsbEnabled;
        });



        // Backwards Compatibility button is only visible if the device is
        // on at least Knox version 2.5 AND lower than Knox version 2.8 (Knox API 17 to 21)
        if(EnterpriseDeviceManager.getAPILevel() < EnterpriseDeviceManager.KNOX_VERSION_CODES.KNOX_2_8 &&
                EnterpriseDeviceManager.getAPILevel() >= EnterpriseDeviceManager.KNOX_VERSION_CODES.KNOX_2_5) {
            activateBackwardsCompatibleKeyBtn.setVisibility(View.VISIBLE);
        }

        showEnabledPackagesBtn.setOnClickListener(v -> showPackageState(true));
        showDisabledPackagesBtn.setOnClickListener(v -> showPackageState(false));
        enableAllDisabledPackagesBtn.setOnClickListener(v -> enableAllDisabledPackages());
        enablePackageBtn.setOnClickListener(v -> promptUserForPackageToEnableOrDisable(true));
        disablePackageBtn.setOnClickListener(v -> promptUserForPackageToEnableOrDisable(false));
        toggleBatteryOptimizationWhitelistBtn.setOnClickListener(v -> promptUserPackageForBatteryWhitelist());
        toggleForceStopBlacklistBtn.setOnClickListener(v -> promptUserPackageForForceStopBlacklist());
    }

    /**
     * If Admin is activated, deactivate this app as device administrator with no explanation.
     * If Admin is deactivated, present a dialog to activate device administrator for this application.
     */
    private void toggleAdmin() {
        boolean adminActive = mDevicePolicyManager.isAdminActive(mDeviceAdmin);

        if (adminActive) {
            mUtils.log(getResources().getString(R.string.deactivating_admin));
            // Deactivate this application as device administrator
            mDevicePolicyManager.removeActiveAdmin(new ComponentName(this, AdminReceiver.class));
        } else {
            mUtils.log(getResources().getString(R.string.activating_admin));
            // Ask the user to add a new device administrator to the system
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
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
                    mUtils.log(getResources().getString(R.string.admin_cancelled));
                    break;
                // End user accepts the request
                case Activity.RESULT_OK:
                    mUtils.log(getResources().getString(R.string.admin_activated));
                    refreshButtons();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshButtons();
    }

    /**
     *  Update button state
     */
    private void refreshButtons() {
        boolean adminActive = mDevicePolicyManager.isAdminActive(mDeviceAdmin);

        if (!adminActive) {
            mToggleAdminBtn.setText(getString(R.string.activate_admin));

        } else {
            mToggleAdminBtn.setText(getString(R.string.deactivate_admin));
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
        mUtils.log(getResources().getString(R.string.license_progress));
    }

    /**
     * Deactivate a KPE license.
     */
    private void deactivateLicense() {
        // Instantiate the KnoxEnterpriseLicenseManager class to use the deActivateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(this);

        // License deactivation
        licenseManager.deActivateLicense(Constants.KPE_LICENSE_KEY);
        mUtils.log(getResources().getString(R.string.license_progress_deactivate));
    }

    /**
     * Call backwards-compatible key activation
     */
    private void activateBackwardsCompatibleKey() {
        // Get an instance of the License Manager
        EnterpriseLicenseManager backwardsCompatibleKeyManager = EnterpriseLicenseManager.getInstance(this);

        // Activate the backwards-compatible license key
        backwardsCompatibleKeyManager.activateLicense(Constants.BACKWARDS_COMPATIBLE_KEY);
        mUtils.log(getResources().getString(R.string.backwards_compatible_key_activation));
    }


    /**
     *  Output to the log view all the enabled/disabled apps.
     *  If state is true, shows enabled applications.
     *  If state is false, shows disabled applications.
     */
    private void showPackageState(boolean state) {
        // Instantiate the EnterpriseDeviceManager class
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this);
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        try {
            // getApplicationStateList() gets the list of enabled or disabled applications based on state.
            String[] packageList = appPolicy.getApplicationStateList(state);
            if (packageList == null) {
                // If an empty/null array returns, no enabled/disabled apps received
                if (state) {
                    mUtils.log(getString(R.string.no_enabled_apps));
                }
                else {
                    mUtils.log(getString(R.string.no_disabled_apps));
                }
            } else {
                // If not an empty array, display the contents whether its enabled or disabled apps
                if (state) {
                    mUtils.log(getString(R.string.showing_enabled_apps));
                } else {
                    mUtils.log(getString(R.string.showing_disabled_apps));
                }
                for (String packageName : packageList) {
                    mUtils.log(packageName);
                }
                mUtils.log(getString(R.string.finished_showing_app_list));
            }
        } catch(SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     *  Enable all the disabled applications.
     */
    private void enableAllDisabledPackages() {
        // Instantiate the EnterpriseDeviceManager class
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this);
        // Get the ApplicationPolicy class where the setApplicationStateList method lives
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        try {
            // Get an array of all the disabled packages
            String [] disabledPackages = appPolicy.getApplicationStateList(false);

            // Enable packages in the array received in previous line,
            // get an array of successfully enabled packages
            String [] enabledPackages = appPolicy.setApplicationStateList(disabledPackages, true);
            if (enabledPackages == null) {
                mUtils.log(getString(R.string.failed_enable_all_apps));
            } else {
                for (String enabledPackage : enabledPackages) {
                    mUtils.log(getString(R.string.enabled_package, enabledPackage));
                }
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     * Enable the given app package name, the user will then be able to open the app.
     */
    private void enablePackage(String packageName) {
        // Instantiate the EnterpriseDeviceManager class
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this);
        // Get the ApplicationPolicy class where the setEnableApplication method lives
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        try {
            // Enable the given app package name
            boolean appEnabled = appPolicy.setEnableApplication(packageName);

            mUtils.log(getResources().getString(R.string.enabled_app_result, appEnabled));
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     * Disable the given app package name, the user will no longer be able to open the app.
     */
    private void disablePackage(String packageName) {
        // Instantiate the EnterpriseDeviceManager class
        EnterpriseDeviceManager enterpriseDeviceManager =
                EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        // Get the ApplicationPolicy class where the setDisableApplication method lives
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        try {
            // Disable the given app package name
            boolean appDisabled = appPolicy.setDisableApplication(packageName);

            mUtils.log(getResources().getString(R.string.disabled_app_result, appDisabled));
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     * Adds or remove the given packageName from the battery optimization whitelist, depending
     * on if the whitelist already contains the packageName.
     *
     * Adding applications to the whitelist will whitelist it from Google's Doze mode, app standby,
     * and power saving mode.
     */
    private void toggleBatteryOptimizationWhitelist(String packageName) {
        EnterpriseDeviceManager enterpriseDeviceManager =
                EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        int result;
        String signature = null;

        try {
            signature = getPackageSignature(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            mUtils.processException(e, TAG);
        }

        AppIdentity appIdentity = new AppIdentity(packageName, signature);
        boolean isInWhitelist;

        try {
            List<String> packageNames = appPolicy.getPackagesFromBatteryOptimizationWhiteList();
            isInWhitelist = packageNames.contains(packageName);

            if(isInWhitelist) {
                result = appPolicy.removePackageFromBatteryOptimizationWhiteList(appIdentity);
            } else {
                result = appPolicy.addPackageToBatteryOptimizationWhiteList(appIdentity);
            }

            if(result != ApplicationPolicy.ERROR_NONE) {
                mUtils.log(getResources().getString(R.string.add_or_remove_app_battery_optimization_whitelist_fail,
                        Integer.toString(result), packageName, isInWhitelist));
            } else {
                mUtils.log(getResources().getString(R.string.add_or_remove_app_battery_optimization_whitelist_success,
                        packageName, !isInWhitelist));
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     * Adds or remove the given packageName from the force stop blacklist, depending
     * on if the blacklist already contains the packageName.
     *
     * Adding applications to the blacklist will prevent the user from performing certain stop actions.
     * Stop actions include force stop in Settings app, stopping through 3rd party apps,
     * stopping through a smart manager/device care app, stopping any background process by system
     * and stopping any service from the application.
     */
    private void toggleForceStopBlacklist(String packageName) {
        EnterpriseDeviceManager enterpriseDeviceManager =
                EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        List<String> list = new ArrayList<>();
        list.add(packageName);
        boolean result;
        boolean isInBlacklist;

        try {
            List<String> packageNames = appPolicy.getPackagesFromForceStopBlackList();
            isInBlacklist = packageNames.contains(packageName);

            if(isInBlacklist) {
                result = appPolicy.removePackagesFromForceStopBlackList(list);
            } else {
                result = appPolicy.addPackagesToForceStopBlackList(list);
            }

            if(!result) {
                mUtils.log(getResources().getString(R.string.add_or_remove_app_force_stop_blacklist_fail,
                        packageName, isInBlacklist));
            } else {
                mUtils.log(getResources().getString(R.string.add_or_remove_app_force_stop_blacklist_success,
                        packageName, !isInBlacklist));
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /**
     * Helper function to obtain a package's signature. Can throw a NameNotFoundException.
     */
    private String getPackageSignature(String packageName) throws PackageManager.NameNotFoundException {
        Signature[] signatures;
        Context context = this;
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;

        packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
        signatures = packageInfo.signingInfo.getSigningCertificateHistory();

        return signatures[0].toCharsString();
    }

    /**
     *  Prompt the user for a package name to enable/disable.
     *  If 'state' is true, enable the app. If 'state' is false, disable the app.
     */
    private void promptUserForPackageToEnableOrDisable(final boolean state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog's title to enable or disable depending on state
        if (state) {
            builder.setTitle(getString(R.string.enable_given_app));
        } else {
            builder.setTitle(getString(R.string.disable_given_app));
        }

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.prompt_user_package_name,
                findViewById(R.id.prompt_user_package_name), false);
        final EditText packageNameTxt = viewInflated.findViewById(R.id.packageNameTxt);

        builder.setView(viewInflated);
        builder.setPositiveButton(getString(R.string.option_confirm), (dialog, which) -> {
            // Get the user provided package name
            String packageName = packageNameTxt.getText().toString();

            // Enable or disable the given package name depending on state
            if (state) {
                enablePackage(packageName);
            } else {
                disablePackage(packageName);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.option_cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     *  Prompt the user for a package name to either add/remove it to/from
     *  the battery optimization whitelist
     */
    private void promptUserPackageForBatteryWhitelist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_or_remove_app_battery_optimization_whitelist));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.prompt_user_package_name,
                findViewById(R.id.prompt_user_package_name), false);
        final EditText packageNameTxt = viewInflated.findViewById(R.id.packageNameTxt);

        builder.setView(viewInflated);
        builder.setPositiveButton(getString(R.string.option_confirm), (dialog, which) -> {
            // Get the user provided package name
            String packageName = packageNameTxt.getText().toString();
            toggleBatteryOptimizationWhitelist(packageName);
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.option_cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     *  Prompt the user for a package name to either add/remove it to/from
     *  the force stop blacklist
     */
    private void promptUserPackageForForceStopBlacklist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_or_remove_app_force_stop_blacklist));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.prompt_user_package_name,
                findViewById(R.id.prompt_user_package_name), false);
        final EditText packageNameTxt = viewInflated.findViewById(R.id.packageNameTxt);

        builder.setView(viewInflated);
        builder.setPositiveButton(getString(R.string.option_confirm), (dialog, which) -> {
            // Get the user provided package name
            String packageName = packageNameTxt.getText().toString();
            toggleForceStopBlacklist(packageName);
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.option_cancel), (dialog, which) -> dialog.cancel());
        builder.show();
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

    public void setUsbPortModeMtp(boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(this).getRestrictionPolicy().setUsbMediaPlayerAvailability(isEnabled);
        mUtils.log("Usb Port Mode Mtp is: " + (isUsbEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public void setUsbPortModeDebugging(boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(this).getRestrictionPolicy().setUsbDebuggingEnabled(isEnabled);
        mUtils.log("Usb Port Mode Debugging is: " + (isUsbEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public void setUsbPortModeTethering(boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(this).getRestrictionPolicy().setUsbTethering(isEnabled);
        mUtils.log("Usb Port Mode Tethering is: " + (isUsbEnabled ? "enabled" : "disabled"));
    }

    /*
     * enable or disable USB access
     * standard sdk
     */
    public void setUsbPortModeHostStorage(boolean isEnabled) {
        EnterpriseDeviceManager.getInstance(this).getRestrictionPolicy().allowUsbHostStorage(isEnabled);
        mUtils.log("Usb Port Mode Host Storage is: " + (isUsbEnabled ? "enabled" : "disabled"));
    }
}