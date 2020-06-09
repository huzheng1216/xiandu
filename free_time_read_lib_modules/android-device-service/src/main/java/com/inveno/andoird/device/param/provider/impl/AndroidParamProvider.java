package com.inveno.andoird.device.param.provider.impl;

import android.content.Context;

import com.inveno.andoird.device.param.provider.IAndroidParamProvider;
import com.inveno.andoird.device.param.provider.IAppParamProvider;
import com.inveno.andoird.device.param.provider.IDeviceParamProvider;
import com.inveno.andoird.device.param.provider.IOsParamProvider;

public class AndroidParamProvider implements IAndroidParamProvider {

    private Context applicationContext;

    public AndroidParamProvider(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public IDeviceParamProvider device() {
        return null;
    }

    @Override
    public IOsParamProvider os() {
        return null;
    }

    @Override
    public IAppParamProvider app() {
        return null;
    }
}
