package com.inveno.xiandu.view.init;

import android.app.Activity;
import android.text.TextUtils;

import com.inveno.android.api.bean.AdConfigData;
import com.inveno.android.api.service.InvenoServiceContext;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AppInitViewProxy {

    private Activity hostActivity;
    private AppInitListener initListener;

    public AppInitViewProxy(Activity hostActivity, AppInitListener initListener) {
        this.hostActivity = hostActivity;
        this.initListener = initListener;
    }

    public void init() {
        startInit();
    }

    public boolean isNeedToCheckPermission(){
        return TextUtils.isEmpty(InvenoServiceContext.uid().getUid());
    }


    private void startInit() {
        initUid();
    }

    private void initUid() {
        String uid = InvenoServiceContext.uid().getUid();
        if (TextUtils.isEmpty(uid)) {
            InvenoServiceContext.uid().requestUid().onSuccess(new Function1<String, Unit>() {
                @Override
                public Unit invoke(String s) {
                    initADConfig();
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    doWhenInitFail();
                    return null;
                }
            }).execute();
        } else {
            initADConfig();
        }
    }

    private void initADConfig() {
        AdConfigData adConfigData = InvenoServiceContext.ad().getAdConfigData();
        if (adConfigData == null) {
            InvenoServiceContext.ad().requestAdConfig()
                    .onSuccess(new Function1<AdConfigData, Unit>() {
                        @Override
                        public Unit invoke(AdConfigData configData) {
                            doWhenAllInitDone();
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            doWhenAllInitDone();
                            return null;
                        }
                    })
                    .execute();
        } else {
            doWhenAllInitDone();
        }
    }

    private void doWhenAllInitDone() {
        initListener.onAppInitSuccess();
    }

    private void doWhenInitFail(){
        initListener.onAppInitFail();
    }
}
