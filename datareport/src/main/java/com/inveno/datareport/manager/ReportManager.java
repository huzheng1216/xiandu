package com.inveno.datareport.manager;

import android.content.Context;
import android.util.Log;

import com.inveno.datareport.bean.AppDurationBean;
import com.inveno.datareport.bean.ReportCache;
import com.inveno.datareport.bean.ReadBean;
import com.inveno.datareport.service.ReportService;

import java.util.LinkedHashMap;
import java.util.UUID;

public enum ReportManager {

    INSTANCE;

    AppDurationBean appDurationBean;

    ReadBean readBean;

    ReportCache reportCacheImp;
    ReportCache reportCacheClick;

    int currentType;
    int currentPageId;

    ReportManager() {
        reportCacheImp = new ReportCache();
        reportCacheClick = new ReportCache();
    }

    public void appStart() {
        if (appDurationBean == null) {
            appDurationBean = new AppDurationBean();
            DataManager.INSTANCE.setSid(UUID.randomUUID().toString());
            appDurationBean.startTime = System.currentTimeMillis();
        }
    }

    public void appEnd(Context context, long pid, String upack) {
        if (appDurationBean != null) {
            appDurationBean.endTime = System.currentTimeMillis();
            DataManager.INSTANCE.initPid(pid);
            LinkedHashMap<String, Object> map = DataManager.INSTANCE.reportAppDuration(appDurationBean.startTime,
                    appDurationBean.endTime - appDurationBean.startTime, appDurationBean.endTime, context, upack);
            Log.i("ReportManager", "appEnd json:" + map);
            ReportService.INSTANCE.report(map);
        }
        appDurationBean = null;
        resetSeq();
    }

    public void setUpack(String upack) {
        DataManager.INSTANCE.setUPack(upack);
    }

    public void reportPageImp(int pageId, String upack, Context context, long pid) {
        DataManager.INSTANCE.initPid(pid);
        LinkedHashMap<String, Object> map = DataManager.INSTANCE.reportPageImp(pageId, upack, context);
        Log.i("ReportManager", "reportPageImp json:" + map);
        ReportService.INSTANCE.report(map);
    }

    public void reportBookImp(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context, long pid) {
        if (reportCacheImp.ifCanReport(pageId, type, contentId)) {
            DataManager.INSTANCE.initPid(pid);
            LinkedHashMap<String, Object> map = DataManager.INSTANCE.reportBookImp(pageId, upack, cpack, type, serverTime, contentId, context);
            Log.i("ReportManager", "reportBookImp json:" + map);
            ReportService.INSTANCE.report(map);
        }
    }

    public void reportBookClick(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context, long pid) {
        currentPageId = pageId;
        currentType = type;
        if (reportCacheClick.ifCanReport(pageId, type, contentId)) {
            DataManager.INSTANCE.initPid(pid);
            LinkedHashMap<String, Object> map = DataManager.INSTANCE.reportBookClick(pageId, upack, cpack, type, serverTime, contentId, context);
            Log.i("ReportManager", "reportBookClick json:" + map);
            ReportService.INSTANCE.report(map);
        }
        reportBookImp(pageId,upack,cpack,type,serverTime,contentId,context,pid);
    }

    public void readBookStart(String upack, String cpack, long serverTime, long contentId) {
        if (readBean == null) {
            readBean = new ReadBean();
            readBean.startTime = System.currentTimeMillis();
            readBean.pageId = currentPageId;
            readBean.upack = upack;
            readBean.cpack = cpack;
            readBean.type = currentType;
            readBean.serverTime = serverTime;
            readBean.contentId = contentId;
        }
    }

    public void readBookStartEnd(Context context, long pid) {
        if (readBean != null) {
            DataManager.INSTANCE.initPid(pid);
            LinkedHashMap<String, Object> map = DataManager.INSTANCE.reportReadBookDuration(readBean.pageId, readBean.upack, readBean.cpack,
                    readBean.serverTime, System.currentTimeMillis() - readBean.startTime,
                    readBean.contentId, readBean.startTime, context);
            Log.i("ReportManager", "readBookStartEnd json:" + map);
            ReportService.INSTANCE.report(map);
        }
        readBean = null;
    }

    public void setReferrer(int referrer) {
        DataManager.INSTANCE.setReferrer(referrer);
    }

    public void setLocation(String location) {
        DataManager.INSTANCE.setLocation(location);
    }

    public void resetSeq() {
        DataManager.INSTANCE.setSeq(0);
    }
}
