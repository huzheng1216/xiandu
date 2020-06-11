package com.inveno.android.read.book.api.params;

import android.text.TextUtils;

import com.inveno.android.api.service.InvenoServiceContext;

import java.util.LinkedHashMap;

/**
 * API参数参数辅助类，用来填充公共参数
 * @作者 杨云龙
 */
public class APIParams {
    public static LinkedHashMap<String,Object> fillCommonParam(LinkedHashMap<String,Object> paramMap) {
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            paramMap.put("uid",uid);
        }
        return paramMap;
    }
}
