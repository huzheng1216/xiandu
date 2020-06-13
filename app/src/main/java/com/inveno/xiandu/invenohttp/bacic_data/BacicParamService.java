package com.inveno.xiandu.invenohttp.bacic_data;

import android.text.TextUtils;

import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.basics.service.BasicsServiceModule;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.app.info.AppInfoHolder;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
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
        baseParam.put("product_id", "111111");
        if (ServiceContext.userService().getUserInfo() != null && ServiceContext.userService().getUserInfo().getPid() > 0)
            baseParam.put("pid", ServiceContext.userService().getUserInfo().getPid());
        if (!TextUtils.isEmpty(InvenoServiceContext.uid().getUid()))
            baseParam.put("uid", InvenoServiceContext.uid().getUid());
        baseParam.put("request_time", String.valueOf(System.currentTimeMillis()));
        baseParam.put("app_ver", AppInfoHolder.Companion.getAppInfo().getVersionName());
        baseParam.put("api_ver", "1.0");
        baseParam.put("network", AndroidParamProviderHolder.get().device().getNetwork());
//        baseParam.put("platform","");
//        baseParam.put("brand","");
//        baseParam.put("model","");
//        baseParam.put("tk","");

    }

    public LinkedHashMap<String, Object> getBaseParam() {
        return baseParam;
    }
}
