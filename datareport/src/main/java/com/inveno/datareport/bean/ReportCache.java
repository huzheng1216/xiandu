package com.inveno.datareport.bean;

import java.util.HashMap;

public class ReportCache {

    private HashMap<String,Long> impCacheMap = new HashMap<>();

    public boolean ifCanReport(int  pageId , int type , long contentId){
        String key = ""+pageId+":"+type+":"+contentId;
        Long value = impCacheMap.get(key);
        if (value!=null){
            if ((System.currentTimeMillis() - value) >10*60*1000){
                impCacheMap.put(key,System.currentTimeMillis());
                return true;
            }
        }else {
            impCacheMap.put(key,System.currentTimeMillis());
            return true;
        }
        return false;
    }

}
