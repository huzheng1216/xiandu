package com.inveno.android.device.param.provider;

import android.util.DisplayMetrics;

import java.util.List;

public interface IDeviceParamProvider {
    String getPlatform();
    String getNetwork();
    String getImei();
    String getAid();
    String getBrand();
    String getModel();
    String getOsv();
    String getMcc();
    String getMnc();
    int getCell_id();
    int getLac();
    String getNmcc();
    String getNmnc();
    String getMac();
    List<String> getWiFiMacList();
    String getUserAgent();
    DisplayMetrics getMetrics();

    String getOperator();
}
