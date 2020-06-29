package com.inveno.android.api.api.uid;

import android.text.TextUtils;

import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.basics.service.app.info.AppInfoHolder;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.device.param.provider.IAndroidParamProvider;

import java.util.LinkedHashMap;

public class UidParamsUtil {
    private static final String SDK_VERSION = "3.0.5";
    private static final String API_VERSION = "2.1.0";

    public static LinkedHashMap<String, Object> fillUidParams(LinkedHashMap<String, Object> paramMap) {
        IProductService productService = InvenoServiceContext.product();
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();
        paramMap.put("product_id", productService.getProductId());
        paramMap.put("promotion", productService.getPromotion());
        String request_time = String.valueOf(System.currentTimeMillis());
        //        product_id	YES	string	产品 ID。参见产品product_id表（product_id）
//        promotion	YES	string	推广渠道名，参加promotion值，部分toB产品只有一个推广渠道，该字段可以填成厂家名。
//        uid	YES	string	唯一用户标识，当前uid生成逻辑由服务器统一生成一个唯一标识符返回给客户端。客户端存储保证不丢失。若用户删除，则重新由服务端生成。
        paramMap.put("request_time", request_time);
//        request_time	YES	string	客户端请求的时间戳，Unix时间戳格式
//        fuid	NO	string	针对toB产品，如不使用我方uid，填写第三方用户id

        paramMap.put("app_ver", androidParamProvider.app().getVersionName());
//        app_ver	YES	string	客户端 app 的版本。例如：3.10.7.710；toC:填写APP版本，toB:如果为自身APP则填写自身APP版本，如果集成在第三方的APP中，填写第三方APP 版本。
        paramMap.put("sdk_ver", SDK_VERSION);
        paramMap.put("api_ver", API_VERSION);
//        sdk_ver	NO	string	针对toB集成至第三方APP场景，toB不同形态产品集成至第三方同一个APP。如: 金立-负一屏-卡片式SDK集成卡片式SDK和H5产品，取值为两个产品拼装后的值，（1.2.0#2.3.0）。
//        api_ver	YES	string	数据SDK接口协议版本，如：1.1.0
        paramMap.put("tk", productService.createTkWithoutData(request_time));
//        tk	YES	string	验证码。确保访问的合法性，生成规则参考2.4，区分大小写，请用小写字母
//        network	YES	string	网络wifi/2G/3G/4G/5G/其他移动网络/其他网络，暂不需要对制式作区分。对于批量上报的方式，可取上报时的联网类型。参考network取值
        paramMap.put("network", androidParamProvider.device().getNetwork());
//        imei 	NO	string	手机IMEI号，如：SAMSUNG的一台GT-I9308手机的IMEI是：355065053311001，Android设备必填，iOS设备可选
        if (!TextUtils.isEmpty(androidParamProvider.device().getImei())) {
            paramMap.put("imei", androidParamProvider.device().getImei());
        }
//        aid	NO	string	Android 设备 ID。设备为Android时填写例如：3we4cf325c79f28e，Android设备必填，iOS设备不用
        paramMap.put("aid", androidParamProvider.device().getAid());
//        idfa	NO	string	iOS 设备唯一ID，设备为iOS终端时必填，Android设备不用填写。
//        brand	YES	string	设备品牌。例如：Huawei
        paramMap.put("brand", androidParamProvider.device().getBrand());
//        model	YES	string	设备型号。例如：Mate8
        paramMap.put("model", androidParamProvider.device().getModel());
//        osv	NO	string	操作系统版本。例如：5.0.2
        paramMap.put("osv", androidParamProvider.os().getOsVersion());
//        platform	YES	string	平台类型。取值为 ios/android
        paramMap.put("platform", androidParamProvider.device().getPlatform());
//        language	NO	string	手机终端系统语言，格式 en_US，zh_CN
        paramMap.put("language", androidParamProvider.os().getLang());
//        app_lan	YES	string	app选择的语言，格式同zh_CN，请求时此参数为空则默认返回中文资讯。见app_lan表。
        paramMap.put("app_lan", androidParamProvider.os().getLang());
//        mcc	NO	string	SIM 卡中获取到的移动运营商国家代码
        paramMap.put("mcc", androidParamProvider.device().getMcc());
//        mnc	NO	string	SIM 卡中获取到的移动运营商网络代码
        paramMap.put("nmcc", androidParamProvider.device().getNmcc());
//        nmcc	NO	string	接入的移动网络中获取到的移动运营商国家代码
        paramMap.put("nmnc", androidParamProvider.device().getNmnc());
//        nmnc	NO	string	接入的移动网络中获取到的移动运营商网络代码
        paramMap.put("lac", androidParamProvider.device().getLac());
//        lac	NO	string	从所接入的移动网络中获取的基站定位信息LAC（Location Area Code），如果获取不到则空串。
        paramMap.put("cell_id", androidParamProvider.device().getCell_id());
//        cell_id	NO	string	从所接入的移动网络中获到的基站定位信息Cell-Id，如果获取不到则传空串。
//        longitude	NO	string	从 GPS 定位信息中获取的经度，如果获取不到则传空串。例如：12.123456
//        latitude	NO	string	从 GPS 定位信息中获取的纬度，如果获取不到则传空串。例如：12.123456
        paramMap.put("mac", androidParamProvider.device().getMac());
//        mac	NO	string	WIFI接入点的MAC 地址（注意不是移动设备的MAC，而是 WIFI 接入点的 MAC），多个请用英文逗号分隔，最多不超过20个, 请客户端一定要做url encode。
//* 如果可以扫描到所有可以接入的WIFI热点请把这些热点的MAC都报上来。
//* 如果不能扫描到所有可接入的WIFI热点，那么请把当前接入的WIFI的接入点的 MAC 地址报上来。
        paramMap.put("ua", androidParamProvider.device().getUserAgent());
//        ua	NO	String	服务器对接必传，客户端的UserAgent。必须是客户端通过系统API获取的真实UA，不能自定义，
//        ip	NO	String	服务器对接必传，为客户端真实IP；
//        客户端对接不需要上传
//        screenWidth	NO	String	服务器对接必传，屏幕宽度
//        screenHeight	NO	String	服务器对接必传，屏幕高度
//        screenDensity	NO	String	服务器对接必传，屏幕密度
        return paramMap;
    }

    public static LinkedHashMap<String, Object> fillParams(LinkedHashMap<String, Object> paramMap) {
        IProductService iProductService = InvenoServiceContext.product();
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();

        paramMap.put("product_id", iProductService.getProductId());
        if (!TextUtils.isEmpty(InvenoServiceContext.uid().getUid()))
            paramMap.put("uid", InvenoServiceContext.uid().getUid());
        paramMap.put("request_time", String.valueOf(System.currentTimeMillis()));
        paramMap.put("app_ver", AppInfoHolder.Companion.getAppInfo().getVersionName());
        paramMap.put("api_ver", "1.0");
        paramMap.put("network", androidParamProvider.device().getNetwork());
        paramMap.put("platform", "android");
        paramMap.put("brand", androidParamProvider.device().getBrand());
        paramMap.put("model", androidParamProvider.device().getModel());
        paramMap.put("tk", iProductService.createTkWithoutData(String.valueOf(System.currentTimeMillis())));

        paramMap.put("osv", androidParamProvider.os().getOsVersion());
        paramMap.put("imei", androidParamProvider.device().getImei());
        paramMap.put("aid", androidParamProvider.device().getAid());
        paramMap.put("language", androidParamProvider.os().getLang());
        paramMap.put("mcc", androidParamProvider.device().getMcc());
        paramMap.put("mnc", androidParamProvider.device().getMnc());

        return paramMap;
    }
}
