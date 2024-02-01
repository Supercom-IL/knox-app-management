package com.supercom.knox.appmanagement.application;

import android.content.Context;

public class StateRepository extends PreferencesBase {

    private final String KEY_Activated="KEY_Activated";
    private final String KEY_ActivatedTime="KEY_ActivatedTime";


    public StateRepository(Context context) {
        super(context, "StateRepository");
    }

    public boolean isActivated() {
        return get(KEY_Activated, false);
    }
    public Long getActivatedTime() {
        return get(KEY_ActivatedTime, 0L);
    }
    public void setActivated(boolean activated) {
        boolean hasActivated = isActivated();
        put(KEY_Activated, activated);
        if(activated && !hasActivated) {
            put(KEY_ActivatedTime, System.currentTimeMillis());
        }
    }
}
