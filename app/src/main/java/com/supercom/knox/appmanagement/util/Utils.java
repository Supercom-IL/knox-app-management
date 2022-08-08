package com.supercom.knox.appmanagement.util;

import android.util.Log;
import android.widget.TextView;


/**
 * This is a Utility class that is composed of helper methods not completely relevant to the main feature of this app.
 */
public class Utils {

    private TextView mTextView;
    private String mTag;

    public Utils(TextView view, String className) {
        mTextView = view;
        mTag = className;
    }

    /** Log results to a textView in application UI */
    public void log(String text) {
        mTextView.append(text);
        mTextView.append("\n\n");
        mTextView.invalidate();
        Log.d(mTag,text);
    }

    /** Process the exception */
    public void processException(Exception ex, String TAG) {
        if (ex != null) {
            // present the exception message
            String msg = ex.getClass().getCanonicalName() + ": " + ex.getMessage();
            mTextView.append(msg);
            mTextView.append("\n\n");
            mTextView.invalidate();
            Log.e(TAG, msg);
        }
    }
}