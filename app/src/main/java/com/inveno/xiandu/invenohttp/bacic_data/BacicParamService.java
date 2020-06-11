package com.inveno.xiandu.invenohttp.bacic_data;

import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;

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
        baseParam.put("product_id","111111");
//        baseParam.put("pid","");
//        baseParam.put("uid","");
//        baseParam.put("request_time","");
//        baseParam.put("app_ver","");
//        baseParam.put("api_ver","");
//        baseParam.put("network","");
//        baseParam.put("platform","");
//        baseParam.put("brand","");
//        baseParam.put("model","");
//        baseParam.put("tk","");

    }

    public LinkedHashMap<String, Object> getBaseParam(){
        return baseParam;
    }
}
