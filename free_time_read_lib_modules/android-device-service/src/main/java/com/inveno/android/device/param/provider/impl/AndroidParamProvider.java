package com.inveno.android.device.param.provider.impl;

import android.content.Context;

import com.inveno.android.device.param.provider.IAndroidParamProvider;
import com.inveno.android.device.param.provider.IAppParamProvider;
import com.inveno.android.device.param.provider.IDeviceParamProvider;
import com.inveno.android.device.param.provider.IOsParamProvider;

public class AndroidParamProvider implements IAndroidParamProvider {

    private Context applicationContext;
    private IAppParamProvider appParamProvider;
    private IOsParamProvider osParamProvider;
    private IDeviceParamProvider deviceParamProvider;

    public AndroidParamProvider(Context applicationContext) {
        this.applicationContext = applicationContext;
        appParamProvider = new AppParamProvider(applicationContext);
        osParamProvider = new OsParamProvider();
        deviceParamProvider = new DeviceParamProvider(applicationContext);
    }

    @Override
    public IDeviceParamProvider device() {
        return deviceParamProvider;
    }

    @Override
    public IOsParamProvider os() {
        return osParamProvider;
    }

    @Override
    public IAppParamProvider app() {
        return appParamProvider;
    }
}
