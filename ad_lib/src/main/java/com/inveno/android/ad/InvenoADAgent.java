package com.inveno.android.ad;

import android.content.Context;

import com.donews.b.start.DoNewsAdManagerHolder;
import com.inveno.android.ad.contract.IAdApi;
import com.inveno.android.ad.contract.impl.DonewsAdApiImpl;

public class InvenoADAgent {

    private static class ApiHolder{
        private static final IAdApi AD_API = new DonewsAdApiImpl();
    }

    private static final boolean ONLINE_MODE = false;

    public static void onApplicationCreate(Context applicationContext) {
        DoNewsAdManagerHolder.init(applicationContext, ONLINE_MODE);//false是广告正式环境，true是测试环境，请联系我方运营人员配置，对接默认请使用正式环境false.
    }

    public static IAdApi getAdApi(){
        return ApiHolder.AD_API;
    }
}
