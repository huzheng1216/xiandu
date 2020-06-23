package com.inveno.android.api.service.ad;

import androidx.annotation.Nullable;

import com.inveno.android.api.api.InvenoAPIContext;
import com.inveno.android.api.bean.AdConfigData;
import com.inveno.android.api.bean.Rule_list;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AdService extends BaseSingleInstanceService {
    private AdConfigData adConfigData;

    @Nullable
    public Rule_list getRuleList(String scenario) {
        if (adConfigData != null) {
            List<Rule_list> ruleList = adConfigData.getRule_list();
            for (Rule_list item : ruleList) {
                if (item.getScenario().equals(scenario)) {
                    return item;
                }
            }
        }
        return null;
    }

    public AdConfigData getAdConfigData() {
        return adConfigData;
    }

    public StatefulCallBack<AdConfigData> requestAdConfig() {
        final StatefulCallBack<AdConfigData> uidDataStatefulCallBack =
                InvenoAPIContext.ad().requestAdConfig();
        final BaseStatefulCallBack<AdConfigData> proxy = new BaseStatefulCallBack<AdConfigData>() {
            @Override
            public void execute() {
                uidDataStatefulCallBack.execute();
            }
        };
        uidDataStatefulCallBack.onSuccess(new Function1<AdConfigData, Unit>() {
            @Override
            public Unit invoke(AdConfigData configData) {
                adConfigData = configData;
                proxy.invokeSuccess(configData);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                proxy.invokeFail(integer, s);
                return null;
            }
        });
        return proxy;
    }
}
