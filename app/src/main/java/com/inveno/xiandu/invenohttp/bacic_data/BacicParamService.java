package com.inveno.xiandu.invenohttp.bacic_data;

import android.text.TextUtils;

import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.api.service.product.ProductService;
import com.inveno.android.basics.service.BasicsServiceModule;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.app.info.AppInfoHolder;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.device.param.provider.IAndroidParamProvider;
import com.inveno.android.read.book.api.params.APIParams;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.AppInfoUtils;

import java.util.LinkedHashMap;

/**
 * @author yongji.wang
 * @date 2020/6/9 20:03
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class BacicParamService extends BaseSingleInstanceService {

    private LinkedHashMap<String, Object> baseParam = new LinkedHashMap<>();

    @Override
    protected void onCreate() {
        super.onCreate();
        putParams();
    }

    private void putParams() {
        IProductService iProductService = InvenoServiceContext.product();
        IAndroidParamProvider androidParamProvider = AndroidParamProviderHolder.get();

        String reques_time = String.valueOf(System.currentTimeMillis());
        baseParam.put("product_id", iProductService.getProductId());
        if (!TextUtils.isEmpty(InvenoServiceContext.uid().getUid())) {
            baseParam.put("uid", InvenoServiceContext.uid().getUid());
        }
        baseParam.put("request_time", reques_time);
        baseParam.put("app_ver", AppInfoHolder.Companion.getAppInfo().getVersionName());
        baseParam.put("api_ver", "1.0");
        baseParam.put("network", androidParamProvider.device().getNetwork());
        baseParam.put("platform", "android");
        baseParam.put("brand", androidParamProvider.device().getBrand());
        baseParam.put("model", androidParamProvider.device().getModel());
        baseParam.put("tk", iProductService.createTkWithoutData(reques_time));

        baseParam.put("osv", androidParamProvider.os().getOsVersion());
        baseParam.put("imei", androidParamProvider.device().getImei());
        baseParam.put("aid", androidParamProvider.device().getAid());
        baseParam.put("language", androidParamProvider.os().getLang());
        baseParam.put("mcc", androidParamProvider.device().getMcc());
        baseParam.put("mnc", androidParamProvider.device().getMnc());
    }

    private void refreshPid() {
        baseParam.remove("pid");
        if (ServiceContext.userService().getUserPid() > 0)
            baseParam.put("pid", ServiceContext.userService().getUserPid());
        else {
            baseParam.put("pid", 0);
        }
    }

    //更新Pid
    public void refreshBaseParam() {
        refreshPid();
    }

    public LinkedHashMap<String, Object> getBaseParam() {
        if (ServiceContext.userService().getUserPid() > 0)
            baseParam.put("pid", ServiceContext.userService().getUserPid());
        else {
            baseParam.put("pid", 0);
        }
        return baseParam;
    }
}
