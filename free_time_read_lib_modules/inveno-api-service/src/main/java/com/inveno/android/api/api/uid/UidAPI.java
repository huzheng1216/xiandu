package com.inveno.android.api.api.uid;

import android.text.TextUtils;

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

public class UidAPI extends BaseSingleInstanceService {

    public StatefulCallBack<UidData> requestUid(){
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        final StatefulCallBack<HttpResponse>  responseStatefulCallBack =
                HttpUtil.postForm("https://iai.inveno.com/gate/getuid",param);

        final BaseStatefulCallBack<UidData> uiCallBack = new BaseStatefulCallBack<UidData>() {
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
                        if(TextUtils.isEmpty(data)){
                            uiCallBack.invokeFail(500,"获取UID失败");
                        }else{
                            UidData uidData = JsonUtil.Companion.parseObject(data,UidData.class);
                            if("200".equals(uidData.getCode())){
                                uiCallBack.invokeSuccess(uidData);
                            }else {
                                int resultCode = 500;
                                try {
                                    resultCode = Integer.parseInt(uidData.getCode());
                                }catch (Exception e){}
                                uiCallBack.invokeFail(resultCode,"获取UID失败");
                            }
                        }
                        return null;
                    }
                }
        ).onFail(
                new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer code, String s) {
                        uiCallBack.invokeFail(code,s);
                        return null;
                    }
                }
        );
        return uiCallBack;
    }
}
