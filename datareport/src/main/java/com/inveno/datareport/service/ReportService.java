package com.inveno.datareport.service;

import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;

import java.util.LinkedHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public enum  ReportService {

    INSTANCE;

    String url = "http://121.201.120.128:9000/report/data";

    public void report( LinkedHashMap<String, Object> map){

        MultiTypeHttpStatefulCallBack.INSTANCE
                .<String>newCallBack(new TypeReference<String>() {
                }.getType())
                .atUrl(url)
                .withArg(map)
                .buildCallerCallBack()
                .onSuccess(new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String respone) {
//                        Log.i("ReportManager","respone:"+respone);
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
//                        Log.i("ReportManager","integer:"+integer+"   s:"+s);
                        return null;
                    }
                }).execute();
    }

}
