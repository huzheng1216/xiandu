package com.inveno.xiandu.invenohttp.instancecontext;

import com.inveno.android.basics.service.app.context.InstanceContext;
import com.inveno.xiandu.invenohttp.api.LoginAPI;
import com.inveno.xiandu.invenohttp.api.UpdataUserAPI;
import com.inveno.xiandu.invenohttp.api.VaricationCodeAPI;

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

    public static UpdataUserAPI updataUserAPI() {
        return InstanceContext.get().getInstance(UpdataUserAPI.class);
    }
}
