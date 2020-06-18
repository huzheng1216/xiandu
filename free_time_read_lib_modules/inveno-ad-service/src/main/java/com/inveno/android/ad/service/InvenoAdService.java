package com.inveno.android.ad.service;

import android.content.Context;

import com.inveno.android.ad.InvenoADAgent;
import com.inveno.android.ad.bean.ADInfoWrapper;
import com.inveno.android.ad.contract.param.InfoAdParam;
import com.inveno.android.ad.contract.param.PlaintAdParamUtil;
import com.inveno.android.ad.contract.param.SplashAdParam;
import com.inveno.android.api.bean.AdConfigData;
import com.inveno.android.api.bean.Rule_list;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class InvenoAdService {
    public StatefulCallBack<String> init(final Context applicationContext){
        BaseStatefulCallBack<String> callback = new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                InvenoADAgent.onApplicationCreate(applicationContext);
                initADConfig(this);
            }
        };
        return callback;
    }

    public StatefulCallBack<String> requestSplashAd(SplashAdParam splashAdParam){
        String adSpaceId =  getAdSpaceId(ScenarioManifest.SPLASH);
        PlaintAdParamUtil.setPositionId(splashAdParam,adSpaceId);
        return InvenoADAgent.getAdApi().requestSplashAD(splashAdParam);
    }


    public Rule_list requestInfoAdRuleList(String scenario){
        return InvenoServiceContext.ad().getRuleList(scenario);
    }

    public StatefulCallBack<ADInfoWrapper> requestInfoAd(List<InfoAdParam> infoAdParam){
        // TODO
        return null;
        //        return InvenoADAgent.getAdApi().requestInfoAD(infoAdParam);
    }


    private String getAdSpaceId(String scenario){
        Rule_list rule_list = InvenoServiceContext.ad().getRuleList(scenario);
        String adSpaceId = "";
        try {
            adSpaceId = rule_list.getRule().get(0).get(0).getAdspace_id();
        }catch (Exception e){}
        return adSpaceId;
    }

    private void initADConfig(final BaseStatefulCallBack<String> statefulCallBack) {
        AdConfigData adConfigData = InvenoServiceContext.ad().getAdConfigData();
        if (adConfigData == null) {
            InvenoServiceContext.ad().requestAdConfig()
                    .onSuccess(new Function1<AdConfigData, Unit>() {
                        @Override
                        public Unit invoke(AdConfigData configData) {
                            statefulCallBack.invokeSuccess("ok");
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            statefulCallBack.invokeFail(integer,s);
                            return null;
                        }
                    })
                    .execute();
        } else {
            statefulCallBack.invokeSuccess("ok");
        }
    }
}
