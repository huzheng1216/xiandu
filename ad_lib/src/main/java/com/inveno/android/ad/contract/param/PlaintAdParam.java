package com.inveno.android.ad.contract.param;

import android.app.Activity;

public class PlaintAdParam {
    private String positionId;
    private Activity activity;

    public String getPositionId() {
        return positionId;
    }

    void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
