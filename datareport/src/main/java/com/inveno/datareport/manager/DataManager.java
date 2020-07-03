package com.inveno.datareport.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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

import java.util.LinkedHashMap;
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
        initUid();

        drBaseBean.setProduct_id(productService.getProductId());
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
        drBaseBean.setAid(androidParamProvider.device().getAid());
        drBaseBean.setPlatform(androidParamProvider.device().getPlatform());

    }

    private void initUid(){
        String uid = InvenoServiceContext.uid().getUid();
        drBaseBean.setUid(uid);
    }

    public void initPid(long pid) {
        drBaseBean.setPid(pid);
    }

    public void setSid(String sid) {
        drBaseBean.setSid(sid);
    }

    public void setUPack(String upack) {
        drBaseBean.setUpack(upack);
    }

    public void setSeq(int seq){
        drBaseBean.setSeq(seq);
    }

    public void setReferrer(int referrer) {
        drBaseBean.setReferrer(referrer);
    }

    public void setLocation(String location){
        drBaseBean.setLocation(location);
    }

    public  LinkedHashMap<String, Object> reportPageImp(int pageId, String upack, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context, dataReportBean);
        dataReportBean.setEvent_id(EventIdType.PAGE_IMP);
        dataReportBean.setPage_id(pageId);

//        Log.i("ReportManager"," upack" + upack + "  "+TextUtils.isEmpty(upack));
        dataReportBean.setUpack(upack);

        return parseToMap(drBaseBean, dataReportBean);
    }


    public  LinkedHashMap<String, Object> reportBookImp(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context, dataReportBean);
        dataReportBean.setEvent_id(EventIdType.BOOK_IMP);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);
        dataReportBean.setContent_id(contentId);

        return parseToMap(drBaseBean, dataReportBean);
    }


    public  LinkedHashMap<String, Object> reportBookClick(int pageId, String upack, String cpack, int type, long serverTime, long contentId, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context, dataReportBean);
        dataReportBean.setEvent_id(EventIdType.BOOK_CLICK);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);
        dataReportBean.setContent_id(contentId);

        return parseToMap(drBaseBean, dataReportBean);
    }


    public  LinkedHashMap<String, Object> reportReadBookDuration(int pageId, String upack, String cpack, int type, long serverTime, long stayTime, long contentId, long eventTime, Context context) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context, dataReportBean);
        dataReportBean.setEvent_id(EventIdType.READ_BOOK_DURATION);
        dataReportBean.setPage_id(pageId);
        dataReportBean.setUpack(upack);
        dataReportBean.setCpack(cpack);
        dataReportBean.setType(type);
        dataReportBean.setServer_time(serverTime);
        dataReportBean.setStay_time(stayTime);
        dataReportBean.setContent_id(contentId);
        dataReportBean.setEvent_time(eventTime);

        return parseToMap(drBaseBean, dataReportBean);
    }


    public  LinkedHashMap<String, Object> reportAppDuration(long startTime , long stayTime, long leaveTime, Context context , String upack) {
        DataReportBean dataReportBean = new DataReportBean();

        setData(context, dataReportBean);
        dataReportBean.setEvent_id(EventIdType.APP_DURATION);
        dataReportBean.setStay_time(stayTime);
        dataReportBean.setLeave_time(leaveTime);
        dataReportBean.setEvent_time(startTime);
        dataReportBean.setUpack(upack);

        return parseToMap(drBaseBean, dataReportBean);
    }


    private void setData(Context context, DataReportBean dataReportBean) {
        initUid();
        long now = System.currentTimeMillis();
        dataReportBean.setEvent_time(now);
        dataReportBean.setTk(InvenoServiceContext.product().createTk("",""+now));
        dataReportBean.setIp(NetWorkUtil.getIpAddress(context));
        dataReportBean.setRequest_time(String.valueOf(now));
    }

    private LinkedHashMap<String, Object> parseToMap(DrBaseBean drBaseBean, DataReportBean dataReportBean) {
        int seq = drBaseBean.increaseSeq();
        JSONObject jsonObject = (JSONObject) JSON.toJSON(drBaseBean);
        jsonObject.put("seq",seq);

        //一段奇怪的代码
        String newJson = JSON.toJSONString(dataReportBean);
        JSONObject jsonObject1 = JSON.parseObject(newJson);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.putAll(jsonObject);
        map.putAll(jsonObject1);
        return map;
    }

}
