package com.inveno.android.ad.contract.listener;

public interface SplashAdListener {
    void onNoAD(String var1);

    void onClicked();

    void onShow();

    void onPresent();

    void onADDismissed();

    void extendExtra(String var1);
}
