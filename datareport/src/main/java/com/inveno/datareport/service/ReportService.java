package com.inveno.datareport.service;

import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.example.datareport.BuildConfig;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.datareport.manager.ReReportManager;

import java.util.LinkedHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public enum  ReportService {

    INSTANCE;

    String url = BuildConfig.InvenoUrl + "/report/data";

    public void report(final LinkedHashMap<String, Object> map){

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
                        ReReportManager.INSTANCE.putCache(map);
                        return null;
                    }
                }).execute();
    }


    public void report(final LinkedHashMap<String, Object> map , final Callback callback){

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
                        callback.onSuccess(map);
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
//                        Log.i("ReportManager","integer:"+integer+"   s:"+s);

                        callback.onFail(map);
                        return null;
                    }
                }).execute();
    }

    public interface Callback{
        void onSuccess(LinkedHashMap<String, Object> map);
        void onFail(LinkedHashMap<String, Object> map);
    }

}
