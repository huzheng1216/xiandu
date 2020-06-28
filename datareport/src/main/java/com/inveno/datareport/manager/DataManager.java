package com.inveno.datareport.manager;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.hash.Hashing;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.device.param.provider.IAndroidParamProvider;
import com.inveno.android.device.param.provider.tools.NetWorkUtil;
import com.inveno.datareport.bean.DataReportBean;
import com.inveno.datareport.config.Config;
import com.inveno.datareport.config.EventIdType;

import java.util.UUID;

import kotlin.text.Charsets;

public enum DataManager {

    INSTANCE();

    private DataReportBean dataReportBean = new DataReportBean();

    DataManager() {
        init();
    }

    private void init() {
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();
        IProductService productService = InvenoServiceContext.product();
        String uid = InvenoServiceContext.uid().getUid();
        if (!TextUtils.isEmpty(uid)) {
            dataReportBean.setUid(uid);
        }

        dataReportBean.setPid("0");
        dataReportBean.setApp_ver(androidParamProvider.app().getVersionName());
        dataReportBean.setApi_ver(Config.API_VER);
        dataReportBean.setNetwork(androidParamProvider.device().getNetwork());
        dataReportBean.setImei(androidParamProvider.device().getImei());
        dataReportBean.setBrand(androidParamProvider.device().getBrand());
        dataReportBean.setModel(androidParamProvider.device().getModel());
        dataReportBean.setOsv(androidParamProvider.device().getOsv());
        dataReportBean.setLanguage(androidParamProvider.os().getLang());
        dataReportBean.setMcc(androidParamProvider.device().getMcc());
        dataReportBean.setMnc(androidParamProvider.device().getMnc());

        dataReportBean.setReferrer("");

    }

    public void initPid(String pid) {
        dataReportBean.setPid(pid);
    }

    public void setSid(String sid){
        dataReportBean.setSid(sid);
    }

    public void setUPack(String upack){
        dataReportBean.setUpack(upack);
    }

    private static String createAdTk(String productId, String uid, long time) {
        return Hashing.md5().newHasher().putString(productId + ":" + uid + ":" + time, Charsets.UTF_8).hash().toString();
    }

    public void reportPageImp(int pageId, String upack, Context context) {
        dataReportBean.reset();

        setData(context);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);

        dataReportBean.setUpack(upack);


    }


    public void reportBookImp(int pageId, String upack, String cpack, int type, long serverTime, Context context) {
        dataReportBean.reset();

        setData(context);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);


    }


    public void reportBookClick(int pageId, String upack, String cpack, int type, long serverTime, Context context) {
        dataReportBean.reset();

        setData(context);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);


    }


    public void reportReadBookDuration(int pageId, String upack, String cpack, int type, long serverTime, long stayTime, Context context) {
        dataReportBean.reset();

        setData(context);
        dataReportBean.setEvent_id(EventIdType.READ_BOOK_DURATION);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);
        dataReportBean.setStay_time(stayTime);

    }



    public String reportAppDuration(long stayTime, long leaveTime , Context context) {
        dataReportBean.reset();

        setData(context);
        dataReportBean.setEvent_id(EventIdType.READ_BOOK_DURATION);
        dataReportBean.setStay_time(stayTime);
        dataReportBean.setLeave_time(leaveTime);

        return JSON.toJSONString(dataReportBean);
    }


    private void setData(Context context) {
        long now = System.currentTimeMillis();
        dataReportBean.setReport_time(now);
        dataReportBean.setEvent_time(now);
        dataReportBean.setTk(createAdTk(dataReportBean.getProduct_id(), dataReportBean.getUid(), now));
        dataReportBean.setIp(NetWorkUtil.getIpAddress(context));
    }

}
