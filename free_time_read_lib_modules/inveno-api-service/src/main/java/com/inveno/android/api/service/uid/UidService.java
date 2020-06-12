package com.inveno.android.api.service.uid;

import android.text.TextUtils;

import com.inveno.android.api.api.InvenoAPIContext;
import com.inveno.android.api.bean.UidData;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.persist.AppPersistRepository;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class UidService extends BaseSingleInstanceService {
    private String uid;

    @Override
    protected void onCreate() {
        super.onCreate();
        uid = AppPersistRepository.get().get("user_uid");
    }

    public String getUid(){
        return uid;
    }

    public StatefulCallBack<String> requestUid(){
        final StatefulCallBack<UidData> uidDataStatefulCallBack =
                InvenoAPIContext.uid().requestUid();
        final BaseStatefulCallBack<String> proxy = new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                uidDataStatefulCallBack.execute();
            }
        };
        uidDataStatefulCallBack.onSuccess(new Function1<UidData, Unit>() {
            @Override
            public Unit invoke(UidData uidData) {
                uid = uidData.getUid();
                saveUid();
                if(TextUtils.isEmpty(uid)){
                    proxy.invokeFail(500,"获取UID失败");
                }else{
                    proxy.invokeSuccess(uid);
                }
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                proxy.invokeFail(integer,s);
                return null;
            }
        });
        return proxy;
    }

    private void saveUid(){
        AppPersistRepository.get().save("user_uid",uid);
    }
}
