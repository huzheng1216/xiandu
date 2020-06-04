package com.inveno.xiandu.http.body;



import com.inveno.xiandu.http.base.BaseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求支付的配置
 * Created by zheng.hu on 2016/11/7.
 */
public class PayBody extends BaseBody {

    private String app;//微信/支付宝
    private int amount;//支付总数

    public PayBody(String app, int amount){
        this.app = app;
        this.amount = amount;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(10);
        map.put("app",app);
        map.put("model","app");
        map.put("total_amount",amount+"");
        return map;
    }
}
