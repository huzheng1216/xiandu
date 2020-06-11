package com.inveno.android.api.api.ad;

import android.text.TextUtils;

import com.inveno.android.api.api.uid.UidParamsUtil;
import com.inveno.android.api.service.InvenoServiceContext;

import java.util.LinkedHashMap;

public class AdParamUtil {
    public static LinkedHashMap<String,Object> fillParams(LinkedHashMap<String,Object> paramMap) {
        String uid = InvenoServiceContext.uid().getUid();
        UidParamsUtil.fillUidParams(paramMap);
        if(!TextUtils.isEmpty(uid)){
            paramMap.put("uid",uid);
        }
        return paramMap;
    }
}
