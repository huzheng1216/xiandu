package com.inveno.ad_lib.test.testdata;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.donews.b.main.DoNewsAdNative;
import com.donews.b.main.info.DoNewsAD;
import com.donews.b.start.DoNewsAdManagerHolder;

public class DoNewUtils {
    //初始化
    public static void initDoNew(Context context){
        DoNewsAdManagerHolder.init(context, true);//false是广告正式环境，true是测试环境，请联系我方运营人员配置，对接默认请使用正式环境false.
    }

    //开屏广告
    public static void splashAd(Activity activity, ViewGroup view, DoNewsAdNative.SplashListener splashListenerImpl){
        DoNewsAD doNewsAD = new DoNewsAD.Builder()
                .setPositionid("分配的广告位id")
                .setView(view)
                //.setExtendExtra("透传参数")
                .build();

        DoNewsAdNative doNewsAdNative = DoNewsAdManagerHolder.get().createDoNewsAdNative();
        doNewsAdNative.onCreateAdSplash(activity,doNewsAD,splashListenerImpl);
    }
}
