package com.inveno.android.ad.contract;

import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.contract.param.InfoAdParam;
import com.inveno.android.ad.contract.param.RewardAdParam;
import com.inveno.android.ad.contract.param.SplashAdParam;
import com.inveno.android.ad.contract.param.UiAdParam;
import com.inveno.android.basics.service.callback.StatefulCallBack;

public interface IAdApi {

    StatefulCallBack<IndexedAdValueWrapper> requestInfoAD(InfoAdParam adParam);

    StatefulCallBack<String> requestUiAD(UiAdParam uiAdParam);

    StatefulCallBack<String> requestRewardVideoAD(RewardAdParam rewardAdParam);

    StatefulCallBack<String> requestSplashAD(SplashAdParam splashAdParam);

}
