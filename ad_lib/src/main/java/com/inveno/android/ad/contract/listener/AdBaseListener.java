package com.inveno.android.ad.contract.listener;

public interface AdBaseListener {
    void onAdError(int code,String message);

    void onAdLoaded();

    void onAdStatus(int var1, Object var2);
}
