package com.inveno.andoird.device.param.provider;

public interface IAndroidParamProvider {
    IDeviceParamProvider device();
    IOsParamProvider os();
    IAppParamProvider app();
}
