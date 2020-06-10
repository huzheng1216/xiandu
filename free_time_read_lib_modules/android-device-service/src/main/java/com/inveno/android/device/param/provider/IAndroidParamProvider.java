package com.inveno.android.device.param.provider;

public interface IAndroidParamProvider {
    IDeviceParamProvider device();
    IOsParamProvider os();
    IAppParamProvider app();
}
