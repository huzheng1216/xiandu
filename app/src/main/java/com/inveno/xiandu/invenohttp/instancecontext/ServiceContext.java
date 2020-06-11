package com.inveno.xiandu.invenohttp.instancecontext;

import com.inveno.android.basics.service.app.context.InstanceContext;
import com.inveno.xiandu.invenohttp.bacic_data.BacicParamService;
import com.inveno.xiandu.invenohttp.service.UserService;

/**
 * @author yongji.wang
 * @date 2020/6/8 20:25
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ServiceContext {
    public static UserService userService() {
        return InstanceContext.get().getInstance(UserService.class);
    }

    public static BacicParamService bacicParamService() {
        return InstanceContext.get().getInstance(BacicParamService.class);
    }
}
