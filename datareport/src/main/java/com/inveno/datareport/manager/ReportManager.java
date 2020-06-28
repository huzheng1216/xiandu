package com.inveno.datareport.manager;

import android.content.Context;
import android.util.Log;

import com.inveno.datareport.bean.AppDurationBean;

import java.util.UUID;

public enum  ReportManager {

    INSTANCE;

    DataManager dataManager = DataManager.INSTANCE;

    AppDurationBean appDurationBean;

    ReportManager(){

    }

    public void appStart(){
        if (appDurationBean==null) {
            appDurationBean = new AppDurationBean();
            dataManager.setSid(UUID.randomUUID().toString());
            appDurationBean.startTime = System.currentTimeMillis();
        }

    }

    public void appEnd(Context context){
        if (appDurationBean!=null) {
            appDurationBean.endTime = System.currentTimeMillis();
            String json = dataManager.reportAppDuration("", appDurationBean.endTime - appDurationBean.startTime, appDurationBean.endTime, context);
            Log.i("ReportManager", "appEnd json:" + json);
        }
        appDurationBean = null;
    }
}
