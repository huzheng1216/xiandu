package com.inveno.xiandu.http.body;



import com.inveno.xiandu.http.base.BaseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求登录的配置
 * Created by zheng.hu on 2016/11/7.
 */
public class LandingCodeBody extends BaseBody {

    private String app;//微信/支付宝
    private String auth_code;
    private String user_id;

    public LandingCodeBody(String app, String auth_code, String user_id){
        this.app = app;
        this.auth_code = auth_code;
        this.user_id = user_id;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(10);
        map.put("app",app);
        map.put("auth_code",auth_code);
        map.put("user_id",user_id);
        return map;
    }
}
