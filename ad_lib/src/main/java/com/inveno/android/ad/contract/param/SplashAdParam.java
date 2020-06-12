package com.inveno.android.ad.contract.param;

import com.inveno.android.ad.contract.listener.SplashAdListener;

public class SplashAdParam extends UiAdParam{
    private SplashAdListener splashAdListener;

    public SplashAdListener getSplashAdListener() {
        return splashAdListener;
    }

    public void setSplashAdListener(SplashAdListener splashAdListener) {
        this.splashAdListener = splashAdListener;
    }

}
