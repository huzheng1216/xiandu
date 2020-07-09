package com.inveno.xiandu.config;

import com.inveno.xiandu.BuildConfig;

/**
 * Created by Administrator on 2016/9/23.
 */
public class HttpConfig {

    public static final String HOST = BuildConfig.InvenoUrl;
//    public static final String HOST = "http://business.inveno.com/";

    //微信token接口
    public static final String WX_ACCESS_TOKEN_HOST = "https://api.weixin.qq.com";

    //腾讯云上传
    public static final String TENCENT_YUN_UPLOAD = "https://api.weixin.qq.com";
}
