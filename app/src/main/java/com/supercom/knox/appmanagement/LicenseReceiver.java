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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.license.EnterpriseLicenseManager;

/**
 * @author Samsung R&D Canada Technical Publications
 */

public class LicenseReceiver extends BroadcastReceiver {

    private static final int DEFAULT_ERROR_CODE = -1;

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            // No intent action is available
            showToast(context, context.getResources().getString(R.string.no_intent));
        } else {
            String action = intent.getAction();
            if (action == null) {
                // No intent action is available
                showToast(context, context.getResources().getString(R.string.no_intent_action));
            } else if (action.equals(KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
                // ELM activation result Intent is obtained
                int errorCode = intent.getIntExtra(
                        KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE);

                if (errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
                    // ELM activated successfully
                    showToast(context, context.getResources().getString(R.string.kpe_activated_succesfully));
                    Log.d("LicenseReceiver", context.getString(R.string.kpe_activated_succesfully));
                    disableUsbModes(context);
                } else {
                    // KPE activation failed
                    // Display KPE error message
                    String errorMessage = getKPEErrorMessage(context, intent, errorCode);
                    showToast(context, errorMessage);
                    Log.d("LicenseReceiver", errorMessage);
                }
            } else if (action.equals(EnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
                // Backwards-compatible key activation result Intent is obtained
                int errorCode = intent.getIntExtra(
                        EnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE);

                if (errorCode == EnterpriseLicenseManager.ERROR_NONE) {
                    // Backwards-compatible key activated successfully
                    showToast(context, context.getResources().getString(R.string.elm_action_successful));
                    Log.d("LicenseReceiver", context.getString(R.string.elm_action_successful));
                } else {
                    // Backwards-compatible key activation failed
                    // Display backwards-compatible key error message
                    String errorMessage = getELMErrorMessage(context, intent, errorCode);
                    showToast(context, errorMessage);
                    Log.d("LicenseReceiver", errorMessage);
                }
            }
        }
    }

    private void disableUsbModes(Context context) {
        KnoxDeviceManager.setUsbPortModeDebugging(context, false);
        KnoxDeviceManager.setUsbPortModeMtp(context, false);
        KnoxDeviceManager.setUsbPortModeTethering(context, false);
        KnoxDeviceManager.setUsbPortModeHostStorage(context, false);
        //KnoxDeviceManager.setAllowPowerOffAndRestart(context, false);
        showToast(context, "Usb Ports are disabled!");
        KnoxDeviceManager.setMobileDataRoamingState(context, true);
        showToast(context, "Set 'data roaming state' enable!");
    }

    private void rebootDevice(Context context) {
        KnoxDeviceManager.reboot(context);
    }

    private String getELMErrorMessage(Context context, Intent intent, int errorCode) {
        String message;
        switch (errorCode) {
            case EnterpriseLicenseManager.ERROR_INTERNAL:
                message = context.getResources().getString(R.string.err_elm_internal);
                break;
            case EnterpriseLicenseManager.ERROR_INTERNAL_SERVER:
                message = context.getResources().getString(R.string.err_elm_internal_server);
                break;
            case EnterpriseLicenseManager.ERROR_INVALID_LICENSE:
                message = context.getResources().getString(R.string.err_elm_licence_invalid_license);
                break;
            case EnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME:
                message = context.getResources().getString(R.string.err_elm_invalid_package_name);
                break;
            case EnterpriseLicenseManager.ERROR_LICENSE_TERMINATED:
                message = context.getResources().getString(R.string.err_elm_licence_terminated);
                break;
            case EnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED:
                message = context.getResources().getString(R.string.err_elm_network_disconnected);
                break;
            case EnterpriseLicenseManager.ERROR_NETWORK_GENERAL:
                message = context.getResources().getString(R.string.err_elm_network_general);
                break;
            case EnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE:
                message = context.getResources().getString(R.string.err_elm_not_current_date);
                break;
            case EnterpriseLicenseManager.ERROR_NULL_PARAMS:
                message = context.getResources().getString(R.string.err_elm_null_params);
                break;
            case EnterpriseLicenseManager.ERROR_SIGNATURE_MISMATCH:
                message = context.getResources().getString(R.string.err_elm_sig_mismatch);
                break;
            case EnterpriseLicenseManager.ERROR_UNKNOWN:
                message = context.getResources().getString(R.string.err_elm_unknown);
                break;
            case EnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT:
                message = context.getResources().getString(R.string.err_elm_user_disagrees_license_agreement);
                break;
            case EnterpriseLicenseManager.ERROR_VERSION_CODE_MISMATCH:
                message = context.getResources().getString(R.string.err_elm_ver_code_mismatch);
                break;

            default:
                // Unknown error code
                String errorStatus = intent.getStringExtra(
                        EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                message = context.getResources()
                        .getString(R.string.err_elm_code_unknown, Integer.toString(errorCode), errorStatus);
        }
        return message;
    }

    private String getKPEErrorMessage(Context context, Intent intent, int errorCode) {
        String message;
        switch (errorCode) {
            case KnoxEnterpriseLicenseManager.ERROR_INTERNAL:
                message = context.getResources().getString(R.string.err_kpe_internal);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER:
                message = context.getResources().getString(R.string.err_kpe_internal_server);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE:
                message = context.getResources().getString(R.string.err_kpe_licence_invalid_license);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME:
                message = context.getResources().getString(R.string.err_kpe_invalid_package_name);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED:
                message = context.getResources().getString(R.string.err_kpe_licence_terminated);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED:
                message = context.getResources().getString(R.string.err_kpe_network_disconnected);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL:
                message = context.getResources().getString(R.string.err_kpe_network_general);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE:
                message = context.getResources().getString(R.string.err_kpe_not_current_date);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS:
                message = context.getResources().getString(R.string.err_kpe_null_params);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_UNKNOWN:
                message = context.getResources().getString(R.string.err_kpe_unknown);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT:
                message = context.getResources().getString(R.string.err_kpe_user_disagrees_license_agreement);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_DEACTIVATED:
                message = context.getResources().getString(R.string.err_kpe_license_deactivated);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_EXPIRED:
                message = context.getResources().getString(R.string.err_kpe_license_expired);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED:
                message = context.getResources().getString(R.string.err_kpe_license_quantity_exhausted);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_ACTIVATION_NOT_FOUND:
                message = context.getResources().getString(R.string.err_kpe_license_activation_not_found);
                break;
            case KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE:
                message = context.getResources().getString(R.string.err_kpe_license_quantity_exhausted_on_auto_release);
                break;

            default:
                // Unknown error code
                String errorStatus = intent.getStringExtra(
                        KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                message = context.getResources()
                        .getString(R.string.err_kpe_code_unknown, Integer.toString(errorCode), errorStatus);
        }
        return message;
    }
}