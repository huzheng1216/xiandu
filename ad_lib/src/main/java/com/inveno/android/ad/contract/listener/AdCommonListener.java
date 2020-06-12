package com.inveno.android.ad.contract.listener;

public interface AdCommonListener extends AdBaseListener {
    void onAdClicked();

    void onAdShow();

    void onAdExposure();

    void onAdDismissed();
}
