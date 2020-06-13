package com.inveno.android.ad.contract.param;

import android.view.ViewGroup;

public class UiAdParam extends PlaintAdParam {
    private ViewGroup containerView;

    public ViewGroup getContainerView() {
        return containerView;
    }

    public void setContainerView(ViewGroup containerView) {
        this.containerView = containerView;
    }
}
