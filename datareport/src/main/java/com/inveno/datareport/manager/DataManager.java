package com.inveno.datareport.manager;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.Hashing;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.device.param.provider.IAndroidParamProvider;
import com.inveno.android.device.param.provider.tools.NetWorkUtil;
import com.inveno.datareport.bean.DataReportBean;
import com.inveno.datareport.bean.DrBaseBean;
import com.inveno.datareport.config.Config;
import com.inveno.datareport.config.EventIdType;

import java.util.UUID;

import kotlin.text.Charsets;

public enum DataManager {

    INSTANCE();

    private DrBaseBean drBaseBean = new DrBaseBean();

    DataManager() {
        init();
    }

    private void init() {
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();
        IProductService productService = InvenoServiceContext.product();
        String uid = InvenoServiceContext.uid().getUid();
        if (!TextUtils.isEmpty(uid)) {
            drBaseBean.setUid(uid);
        }

        drBaseBean.setPid("0");
        drBaseBean.setApp_ver(androidParamProvider.app().getVersionName());
        drBaseBean.setApi_ver(Config.API_VER);
        drBaseBean.setNetwork(androidParamProvider.device().getNetwork());
        drBaseBean.setImei(androidParamProvider.device().getImei());
        drBaseBean.setBrand(androidParamProvider.device().getBrand());
        drBaseBean.setModel(androidParamProvider.device().getModel());
        drBaseBean.setOsv(androidParamProvider.device().getOsv());
        drBaseBean.setLanguage(androidParamProvider.os().getLang());
        drBaseBean.setMcc(androidParamProvider.device().getMcc());
        drBaseBean.setMnc(androidParamProvider.device().getMnc());

        drBaseBean.setReferrer("");

    }

    public void initPid(String pid) {
        drBaseBean.setPid(pid);
    }

    public void setSid(String sid){
        drBaseBean.setSid(sid);
    }

    public void setUPack(String upack){
        drBaseBean.setUpack(upack);
    }

    public void setReferrer(String referrer){
        drBaseBean.setReferrer(referrer);
    }

    private static String createAdTk(String productId, String uid, long time) {
        return Hashing.md5().newHasher().putString(productId + ":" + uid + ":" + time, Charsets.UTF_8).hash().toString();
    }

    public String reportPageImp(int pageId, String upack, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context,dataReportBean);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);

        dataReportBean.setUpack(upack);

        return toJsonString(drBaseBean , dataReportBean);
    }


    public String reportBookImp(int pageId, String upack, String cpack, int type, long serverTime, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context,dataReportBean);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);

        return toJsonString(drBaseBean , dataReportBean);
    }


    public String reportBookClick(int pageId, String upack, String cpack, int type, long serverTime, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context,dataReportBean);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);

        return toJsonString(drBaseBean , dataReportBean);
    }


    public String reportReadBookDuration(int pageId, String upack, String cpack, int type, long serverTime, long stayTime, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context,dataReportBean);
        dataReportBean.setEvent_id(EventIdType.READ_BOOK_DURATION);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);
        dataReportBean.setStay_time(stayTime);

        return toJsonString(drBaseBean , dataReportBean);
    }



    public String reportAppDuration(long stayTime, long leaveTime , Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context,dataReportBean);
        dataReportBean.setEvent_id(EventIdType.READ_BOOK_DURATION);
        dataReportBean.setStay_time(stayTime);
        dataReportBean.setLeave_time(leaveTime);

        return toJsonString(drBaseBean , dataReportBean);
    }


    private void setData(Context context , DataReportBean dataReportBean) {
        long now = System.currentTimeMillis();
        dataReportBean.setReport_time(now);
        dataReportBean.setEvent_time(now);
        dataReportBean.setTk(createAdTk(drBaseBean.getProduct_id(), drBaseBean.getUid(), now));
        dataReportBean.setIp(NetWorkUtil.getIpAddress(context));
    }

    private String toJsonString(DrBaseBean drBaseBean , DataReportBean dataReportBean){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(drBaseBean);
        JSONObject jsonObject2 = (JSONObject) JSON.toJSON(dataReportBean);
        jsonObject.putAll(jsonObject2);
        return jsonObject.toJSONString();
    }
}
