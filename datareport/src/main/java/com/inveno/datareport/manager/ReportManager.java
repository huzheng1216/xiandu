package com.inveno.datareport.manager;

import android.content.Context;
import android.util.Log;

import com.inveno.datareport.bean.AppDurationBean;
import com.inveno.datareport.bean.ReadBean;
import com.inveno.datareport.service.ReportService;

import java.util.UUID;

public enum ReportManager {

    INSTANCE;

    AppDurationBean appDurationBean;

    ReadBean readBean;

    ReportManager() {
    }

    public void appStart() {
        if (appDurationBean == null) {
            appDurationBean = new AppDurationBean();
            DataManager.INSTANCE.setSid(UUID.randomUUID().toString());
            appDurationBean.startTime = System.currentTimeMillis();
        }
    }

    public void appEnd(Context context) {
        if (appDurationBean != null) {
            appDurationBean.endTime = System.currentTimeMillis();
            String json = DataManager.INSTANCE.reportAppDuration(appDurationBean.endTime - appDurationBean.startTime, appDurationBean.endTime, context);
            Log.i("ReportManager", "appEnd json:" + json);
            ReportService.INSTANCE.report(json);
        }
        appDurationBean = null;
    }

    public void setUpack(String upack) {
        DataManager.INSTANCE.setUPack(upack);
    }

    public void reportPageImp(int pageId, String upack, Context context) {
        String json = DataManager.INSTANCE.reportPageImp(pageId, upack, context);
        Log.i("ReportManager", "reportPageImp json:" + json);
        ReportService.INSTANCE.report(json);
    }

    public void reportBookImp(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context) {
        String json = DataManager.INSTANCE.reportBookImp(pageId, upack, cpack, type, serverTime, contentId, context);
        Log.i("ReportManager", "reportBookImp json:" + json);
        ReportService.INSTANCE.report(json);
    }

    public void reportBookClick(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context) {
        String json = DataManager.INSTANCE.reportBookClick(pageId, upack, cpack, type, serverTime, contentId, context);
        Log.i("ReportManager", "reportBookClick json:" + json);
        ReportService.INSTANCE.report(json);
    }

    public void readBookStart(int pageId, String upack, String cpack, int type, long serverTime, long contentId) {
        if (readBean == null) {
            readBean = new ReadBean();
            readBean.startTime = System.currentTimeMillis();
            readBean.pageId = pageId;
            readBean.upack = upack;
            readBean.cpack = cpack;
            readBean.type = type;
            readBean.serverTime = serverTime;
            readBean.contentId = contentId;
        }
    }

    public void readBookStartEnd(Context context) {
        if (readBean != null) {
            String json = DataManager.INSTANCE.reportReadBookDuration(readBean.pageId, readBean.upack, readBean.cpack,
                    readBean.type, readBean.serverTime, System.currentTimeMillis() - readBean.startTime,
                    readBean.contentId, readBean.startTime, context);
            Log.i("ReportManager", "readBookStartEnd json:" + json);
            ReportService.INSTANCE.report(json);
        }
        readBean = null;
    }

}
