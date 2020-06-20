package com.inveno.android.ad.contract.impl;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.donews.b.main.DoNewsAdNative;
import com.donews.b.main.info.DoNewsAD;
import com.donews.b.main.info.DoNewsAdNativeData;
import com.donews.b.start.DoNewsAdManagerHolder;
import com.donews.b.start.DoNewsAdView;
import com.inveno.android.ad.bean.ADInfoWrapper;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.contract.IAdApi;
import com.inveno.android.ad.contract.param.InfoAdParam;
import com.inveno.android.ad.contract.param.RewardAdParam;
import com.inveno.android.ad.contract.param.SplashAdParam;
import com.inveno.android.ad.contract.param.UiAdParam;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;

import java.util.ArrayList;
import java.util.List;

public class DonewsAdApiImpl implements IAdApi {
    @Override
    public StatefulCallBack<IndexedAdValueWrapper> requestInfoAD(final InfoAdParam adParam) {
        final BaseStatefulCallBack<IndexedAdValueWrapper> adInfoWrapperBaseStatefulCallBack = new BaseStatefulCallBack<IndexedAdValueWrapper>() {
            @Override
            public void execute() {
                try {
                    executeLoadInfoAd(adParam, this);
                } catch (Exception e) {
                    e.printStackTrace();
                    invokeFail(600, e.getMessage());
                }
            }
        };
        return adInfoWrapperBaseStatefulCallBack;
    }

    @Override
    public StatefulCallBack<String> requestUiAD(UiAdParam uiAdParam) {
        return new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                invokeFail(400, "not support for now");
            }
        };
    }

    @Override
    public StatefulCallBack<String> requestRewardVideoAD(RewardAdParam uiAdParam) {
//        PlaintAdParamUtil.setPositionId()
        return new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                invokeFail(400, "not support for now");
            }
        };
    }

    @Override
    public StatefulCallBack<String> requestSplashAD(final SplashAdParam splashAdParam) {
        return new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                try {
                    if (TextUtils.isEmpty(splashAdParam.getPositionId())) {
                        invokeFail(400, "position_id is empty");
                    } else {
                        executeLoadSplashAd(splashAdParam);
                        invokeSuccess("--------------------------- ok");
                    }
                } catch (Exception e) {
                    invokeFail(400, e.getMessage());
                }
            }
        };
    }


    private void executeLoadInfoAd(final InfoAdParam adParam, final BaseStatefulCallBack<IndexedAdValueWrapper> adInfoWrapperBaseStatefulCallBack) {
        DoNewsAD doNewsAD = new DoNewsAD.Builder()
                .setPositionid(adParam.getPositionId())
                .setExpressViewWidth(adParam.getWidth().floatValue())
                .setExpressViewHeight(adParam.getHeight().floatValue())
                .setAdCount(1)
                .build();
        DoNewsAdNative doNewsAdNative = DoNewsAdManagerHolder.get().createDoNewsAdNative();
        doNewsAdNative.onCreateAdInformation(adParam.getContext(), doNewsAD, new DoNewsAdNative.DoNewsNativesListener() {
            @Override
            public void OnFailed(String s) {//请求广告失败
                adInfoWrapperBaseStatefulCallBack.invokeFail(500, s);
            }

            @Override
            public void Success(List<DoNewsAdNativeData> list) {//请求信息流广告成功
                try {
                    Log.i("requestInfoAd","Success list"+list);
                    if (list.size() > 0) {
//                        List<Object> callBackList = new ArrayList<>();
//                        callBackList.addAll(list);
//                        adParam.getAdTemplateListener().Success(callBackList);

                        IndexedAdValueWrapper indexedAdValueWrapper = new IndexedAdValueWrapper();
                        int adShowIndex = adParam.getIndex();
                        DoNewsAdNativeData adValue = list.get(0);
                        indexedAdValueWrapper.setAdValue(adValue);
                        indexedAdValueWrapper.setIndex(adShowIndex);
                        indexedAdValueWrapper.setScenario(adParam.getScenario());
                        indexedAdValueWrapper.setAdSpaceId(adParam.getAdSpaceId());
                        indexedAdValueWrapper.setDisplayType(adParam.getDisplayType());
                        indexedAdValueWrapper.setViewType(adParam.getViewType());

                        adInfoWrapperBaseStatefulCallBack.invokeSuccess(indexedAdValueWrapper);
                    } else {
                        adInfoWrapperBaseStatefulCallBack.invokeFail(401, "adlist is emptry");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    adInfoWrapperBaseStatefulCallBack.invokeFail(600, e.getMessage());
                }

            }
        });


//        DoNewsAD doNewsAD = new DoNewsAD.Builder()
//                .setExpressViewWidth(adParam.getWidth().floatValue())
//                .setExpressViewHeight(adParam.getHeight().floatValue())
//                .setAdCount(adParam.getAdIndexList().size())
//                .build();
//        DoNewsAdNative doNewsAdNative = DoNewsAdManagerHolder.get().createDoNewsAdNative();
//        doNewsAdNative.onCreatTemplateAd(adParam.getActivity(), doNewsAD, new DoNewsAdNative.DoNewsTemplateListener() {
//            @Override
//            public void onAdError(String s) {
//                adParam.getAdTemplateListener().onAdError(s);
//            }
//
//            @Override
//            public void onNoAD(String s) {
//                adParam.getAdTemplateListener().onNoAD(s);
//            }
//
//            @Override
//            public void onADLoaded(List<DoNewsAdView> list) {

//            }
//
//            @Override
//            public void onAdClose(DoNewsAdView doNewsAdView) {
//
//            }
//
//            @Override
//            public void onADExposure() {
//
//            }
//
//            @Override
//            public void onADClicked() {
//
//            }
//        });
    }


    private void executeLoadSplashAd(final SplashAdParam splashAdParam) {
        DoNewsAD doNewsAD = new DoNewsAD.Builder()
                .setPositionid(splashAdParam.getPositionId()) //"分配的广告位id"
                .setView(splashAdParam.getContainerView())
                //.setExtendExtra("透传参数")
                .build();
        DoNewsAdNative doNewsAdNative = DoNewsAdManagerHolder.get().createDoNewsAdNative();
        doNewsAdNative.onCreateAdSplash(splashAdParam.getActivity(), doNewsAD, new DoNewsAdNative.SplashListener() {
            @Override
            public void onNoAD(String s) {
                Log.i("splashad","onNoAD "+s);
                splashAdParam.getSplashAdListener().onNoAD(s);
            }

            @Override
            public void onClicked() {
                splashAdParam.getSplashAdListener().onClicked();
            }

            @Override
            public void onShow() {
                splashAdParam.getSplashAdListener().onShow();
            }

            @Override
            public void onPresent() {
                splashAdParam.getSplashAdListener().onPresent();
            }

            @Override
            public void onADDismissed() {
                splashAdParam.getSplashAdListener().onADDismissed();
            }

            @Override
            public void extendExtra(String s) {
                splashAdParam.getSplashAdListener().extendExtra(s);
                Log.i("splashad","extendExtra "+s);
            }
        });
    }
}
