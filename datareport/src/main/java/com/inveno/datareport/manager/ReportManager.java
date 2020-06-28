package com.inveno.datareport.manager;

import android.content.Context;
import android.util.Log;

import com.inveno.datareport.bean.AppDurationBean;
import com.inveno.datareport.service.ReportService;

import java.util.UUID;

public enum  ReportManager {

    INSTANCE;

    AppDurationBean appDurationBean;

    ReportManager(){}

    public void appStart(){
        if (appDurationBean==null) {
            appDurationBean = new AppDurationBean();
            DataManager.INSTANCE.setSid(UUID.randomUUID().toString());
            appDurationBean.startTime = System.currentTimeMillis();
        }
    }

    public void appEnd(Context context){
        if (appDurationBean!=null) {
            appDurationBean.endTime = System.currentTimeMillis();
            String json =  DataManager.INSTANCE.reportAppDuration(appDurationBean.endTime - appDurationBean.startTime, appDurationBean.endTime, context);
            Log.i("ReportManager", "appEnd json:" + json);
            ReportService.INSTANCE.report(json);
        }
        appDurationBean = null;
    }

    public void setUpack(String upack){
        DataManager.INSTANCE.setUPack(upack);
    }
}
