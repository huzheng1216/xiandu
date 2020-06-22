package com.inveno.android.ad.contract.param;

import android.app.Activity;
import android.content.Context;

public class PlaintAdParam {
    private String positionId;
    private Context context;
    private Activity activity;

    public String getPositionId() {
        return positionId;
    }

    void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
