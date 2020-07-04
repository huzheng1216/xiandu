package com.inveno.datareport.manager;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inveno.datareport.config.Config;
import com.inveno.datareport.service.ReportService;
import com.inveno.datareport.utils.ReportSPUtils;

import java.util.LinkedHashMap;

public enum ReReportManager {

    INSTANCE;

    boolean isRunning;
    final String REGEX = ";";
    long time;

    public void putCache(LinkedHashMap<String, Object> map) {

        String key = "" + map.get("seq") + map.get("event_id") + map.get("request_time");
        saveCache(key, false);
        String value = JSON.toJSONString(map);
        ReportSPUtils.setInformain(key, value);

    }

    public void checkReReport() {
        if (!isRunning && System.currentTimeMillis() - time > 5 *60 *1000) {
            isRunning = true;
            time = System.currentTimeMillis();
            String cacheKeys = saveCache("",true);
            Log.i("ReportManager", "checkReReport  cacheKeys:"+cacheKeys);
            if (!TextUtils.isEmpty(cacheKeys)) {
                String[] cacheKeysSplit = cacheKeys.split(REGEX);
                final int length = cacheKeysSplit.length;
                final int[] count = new int[1];
                for (int i = 0; i < length; i++) {
                    String key = cacheKeysSplit[i];
                    if (!TextUtils.isEmpty(key)) {
                        String value = ReportSPUtils.getInformain(key, "");
                        if (!TextUtils.isEmpty(value)) {
                            ReportSPUtils.remove(key);
                            JSONObject jsonObject = JSONObject.parseObject(value);
                            final LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
                            linkedHashMap.putAll(jsonObject);
//                            Log.i("ReportManager","checkReReport   linkedHashMap:"+linkedHashMap);
                            ReportService.INSTANCE.report(linkedHashMap, new ReportService.Callback() {
                                @Override
                                public void onSuccess(LinkedHashMap<String, Object> map) {
                                    count[0]++;
                                    if (count[0]>=length){
                                        isRunning = false;
                                    }
                                }

                                @Override
                                public void onFail(LinkedHashMap<String, Object> map) {
                                    count[0]++;
                                    if (count[0]>=length){
                                        isRunning = false;
                                    }
                                    putCache(linkedHashMap);
                                }
                            });
                        }
                    }
                }
            }else {
                isRunning = false;
            }
        }
    }

    public synchronized String saveCache(String key , boolean isClean) {
        String cacheKeys = ReportSPUtils.getInformain(Config.SP_KEY_KEYLIST, "");
        if (isClean){
            ReportSPUtils.setInformain(Config.SP_KEY_KEYLIST, "");
        }else {
            String newCacheKeys = cacheKeys + key + REGEX;
            Log.i("ReportManager", "saveCache  newCacheKeys:"+newCacheKeys);
            ReportSPUtils.setInformain(Config.SP_KEY_KEYLIST, newCacheKeys);
        }
        return cacheKeys;
    }


}
