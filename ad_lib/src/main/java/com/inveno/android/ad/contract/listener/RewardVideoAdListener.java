package com.inveno.android.ad.contract.listener;

public interface RewardVideoAdListener {
    void onAdShow();

    void onAdVideoBarClick();

    void onAdClose();

    void onVideoComplete();

    void onRewardVerify(boolean var1);

    void onSkippedVideo();

    void onError(int var1, String var2);
}
