package com.inveno.andoird.device.param.provider;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.List;

public interface IDeviceParamProvider {
    String getPlatform();
    String getNetwork(Context context);
    String getImei(Context context);
    String getAid(Context context);
    String getBrand();
    String getModel();
    String getOsv();
    String getMcc(Context context);
    String getMnc(Context context);
    int getCell_id(Context context);
    int getLac(Context context);
    String getNmcc(Context context);
    String getNmnc(Context context);
    String getMac(Context context);
    List<String> getWiFiMacList(Context context);
    String getUserAgent(Context context);
    DisplayMetrics getMetrics(Context context);
}
