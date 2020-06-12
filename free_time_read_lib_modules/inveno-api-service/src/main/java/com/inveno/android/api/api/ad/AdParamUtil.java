package com.inveno.android.api.api.ad;

import android.text.TextUtils;

import com.google.common.hash.Hashing;
import com.inveno.android.api.api.uid.UidParamsUtil;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.device.param.provider.IAndroidParamProvider;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import kotlin.text.Charsets;

public class AdParamUtil {

    private static final String AD_SDK_VER = "2.0.0";
    private static final String R_VER = "5";
    private static final String SDK_VER = "3";
    private static final String API_VERSION = "2.0";
    private static final int AD_APPID = 390;

    public static LinkedHashMap<String,Object> fillParams(LinkedHashMap<String,Object> paramMap) {
        String uid = InvenoServiceContext.uid().getUid();
        IProductService productService = InvenoServiceContext.product();
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();
        if(!TextUtils.isEmpty(uid)){
            paramMap.put("uid",uid);
        }
        String request_time = String.valueOf(System.currentTimeMillis());
        //        product_id	YES	string	产品 ID。参见产品product_id表（product_id）
//        promotion	YES	string	推广渠道名，参加promotion值，部分toB产品只有一个推广渠道，该字段可以填成厂家名。
//        uid	YES	string	唯一用户标识，当前uid生成逻辑由服务器统一生成一个唯一标识符返回给客户端。客户端存储保证不丢失。若用户删除，则重新由服务端生成。
        paramMap.put("tm",request_time);
        paramMap.put("adsdk_ver",AD_SDK_VER);
        paramMap.put("tk",createAdTk(productService.getProductId(),uid,request_time)); // productService.createTkWithoutData(request_time)
        paramMap.put("sdk",SDK_VER);
        paramMap.put("mac",androidParamProvider.device().getMac());
        paramMap.put("app_ver_code",androidParamProvider.app().getVersionCode());
        paramMap.put("appid",AD_APPID);
        paramMap.put("pm",androidParamProvider.device().getBrand()+" "+androidParamProvider.device().getModel());
        paramMap.put("op",androidParamProvider.device().getOperator());
        paramMap.put("net",androidParamProvider.device().getNetwork());
        paramMap.put("osver",androidParamProvider.os().getOsVersion());
        paramMap.put("app",productService.getProductId());
        paramMap.put("api_ver",API_VERSION);
        paramMap.put("os",androidParamProvider.device().getPlatform());
        paramMap.put("rver",R_VER);
        paramMap.put("lang",androidParamProvider.os().getLang());
        paramMap.put("ver",androidParamProvider.app().getVersionName());
        return paramMap;
    }

    private static String createAdTk(String productId,String uid,String time){
        return Hashing.md5().newHasher().putString(productId+":"+uid+":"+time, Charsets.UTF_8).hash().toString();
    }
}
