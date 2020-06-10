package com.inveno.android.api.api.ad;

import android.text.TextUtils;

import com.inveno.android.api.api.uid.UidParamsUtil;
import com.inveno.android.api.bean.AdConfigData;
import com.inveno.android.api.bean.UidData;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.android.basics.service.third.network.HttpResponse;
import com.inveno.android.basics.service.third.network.HttpUtil;

import java.util.LinkedHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AdAPI extends BaseSingleInstanceService {
    //https://business.inveno.com/ht_adui
    public StatefulCallBack<AdConfigData> requestAdConfig() {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        final StatefulCallBack<HttpResponse> responseStatefulCallBack =
                HttpUtil.postForm("https://business.inveno.com/ht_adui", param);

        final BaseStatefulCallBack<AdConfigData> uiCallBack = new BaseStatefulCallBack<AdConfigData>() {
            @Override
            public void execute() {
                responseStatefulCallBack.execute();
            }
        };
        responseStatefulCallBack.onSuccess(
                new Function1<HttpResponse, Unit>() {
                    @Override
                    public Unit invoke(HttpResponse httpResponse) {
                        String data = new String(httpResponse.getData());
                        if (TextUtils.isEmpty(data)) {
                            uiCallBack.invokeFail(500, "获取广告配置失败");
                        } else {
                            AdConfigData adConfigData = JsonUtil.Companion.parseObject(data, AdConfigData.class);
                            if(adConfigData == null || adConfigData.getRule_list() == null){
                                uiCallBack.invokeFail(500, "获取广告配置失败");
                            }else{
                                uiCallBack.invokeSuccess(adConfigData);
                            }
                        }
                        return null;
                    }
                }
        ).onFail(
                new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer code, String s) {
                        uiCallBack.invokeFail(code, s);
                        return null;
                    }
                }
        );
        return uiCallBack;
    }
}
