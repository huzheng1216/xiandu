package com.inveno.xiandu.view.user.login.service;

import com.inveno.android.basics.service.app.context.InstanceContext;
import com.inveno.xiandu.view.user.login.network.VaricationCodeAPI;

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
}
