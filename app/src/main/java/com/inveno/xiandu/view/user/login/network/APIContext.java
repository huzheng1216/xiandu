package com.inveno.xiandu.view.user.login.network;

import com.inveno.android.basics.service.app.context.InstanceContext;

/**
 * @author yongji.wang
 * @date 2020/6/8 17:13
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class APIContext {

    public static VaricationCodeAPI varicationCode() {
        return InstanceContext.get().getInstance(VaricationCodeAPI.class);
    }

    public static LoginAPI loginAPI() {
        return InstanceContext.get().getInstance(LoginAPI.class);
    }
}
