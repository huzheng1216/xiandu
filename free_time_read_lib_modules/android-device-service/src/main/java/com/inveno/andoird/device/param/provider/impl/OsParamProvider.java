package com.inveno.andoird.device.param.provider.impl;

import com.inveno.andoird.device.param.provider.IOsParamProvider;
import com.inveno.andoird.device.param.provider.tools.DeviceUtils;

/**
 * 设系统参数实现类
 * @author yongji.wang
 * @date 2020/6/4 14:57
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class OsParamProvider implements IOsParamProvider {
    @Override
    public String getOsVersion() {
        return DeviceUtils.getSystemVersion();
    }

    @Override
    public String getLang() {
        return DeviceUtils.getSystemLanguage();
    }
}
