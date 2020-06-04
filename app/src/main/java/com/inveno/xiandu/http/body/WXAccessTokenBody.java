package com.inveno.xiandu.http.body;


import com.inveno.xiandu.http.base.BaseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 37399 on 2016/11/5.
 */
public class WXAccessTokenBody extends BaseBody {

    private String appid;
    private String secret;
    private String code;
    private String grant_type = "authorization_code";

    public WXAccessTokenBody(String appid, String secret, String code){
        this.appid = appid;
        this.secret = secret;
        this.code = code;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(10);
        map.put("appid", this.appid);
        map.put("secret", this.secret);
        map.put("code", this.code);
        map.put("grant_type", this.grant_type);
        return map;
    }
}
