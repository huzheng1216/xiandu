package com.inveno.andoird.device.param.provider.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.inveno.andoird.device.param.provider.IDeviceParamProvider;
import com.inveno.andoird.device.param.provider.tools.DeviceConfig;
import com.inveno.andoird.device.param.provider.tools.DeviceUtils;
import com.inveno.andoird.device.param.provider.tools.TelephonyManagerTools;

import java.util.List;

/**
 * 设备参数实现类
 *
 * @author yongji.wang
 * @date 2020/6/4 14:57
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class DeviceParamProvider implements IDeviceParamProvider {

    /**
     * 获取平台
     *
     * @return
     */
    @Override
    public String getPlatform() {
        return "Android";
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    @Override
    public String getNetwork(Context context) {
        return TelephonyManagerTools.GetNetworkType(context);
    }

    /**
     * 获取imei
     *
     * @param context
     * @return
     */
    @Override
    public String getImei(Context context) {
        return TelephonyManagerTools.getImei(context);
    }

    @Override
    public String getAid(Context context) {
        return DeviceUtils.getAndroidId(context);
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    @Override
    public String getBrand() {
        return DeviceUtils.getDeviceBrand();
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    @Override
    public String getModel() {
        return DeviceUtils.getSystemModel();
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    @Override
    public String getOsv() {
        return DeviceUtils.getSystemVersion();
    }

    /**
     * 获取sim卡移动运营商国家代码
     *
     * @return
     */
    @Override
    public String getMcc(Context context) {
        if (TextUtils.isEmpty(TelephonyManagerTools.getImsi(context))) {
            return "";
        }
        return TelephonyManagerTools.getImsi(context).substring(3, 5);
    }

    /**
     * 获取sim卡移动运营商网络代码
     *
     * @return
     */
    @Override
    public String getMnc(Context context) {
        if (TextUtils.isEmpty(TelephonyManagerTools.getImsi(context))) {
            return "";
        }
        return TelephonyManagerTools.getImsi(context).substring(3, 5);
    }

    @Override
    public int getCell_id(Context context) {
        return TelephonyManagerTools.getGsmLocation(context).getCell_id();
    }

    @Override
    public int getLac(Context context) {
        return TelephonyManagerTools.getGsmLocation(context).getLac();
    }

    /**
     * 获取当前网络移动运营商国家代码
     *
     * @return
     */
    @Override
    public String getNmcc(Context context) {
        return TelephonyManagerTools.getGsmLocation(context).getNmcc();
    }

    /**
     * 获取当前网络移动运营商网络代码
     *
     * @return
     */
    @Override
    public String getNmnc(Context context) {
        return TelephonyManagerTools.getGsmLocation(context).getNmnc();
    }

    /**
     * 扫描到的WIFI的MAC地址
     *
     * @return
     */
    @Override
    public String getMac(Context context) {
        return TelephonyManagerTools.getMacAddress(context);
    }

    /**
     * 扫描到的WIFI的Mac地址列表的集合
     *
     * @return
     */
    @Override
    public List<String> getWiFiMacList(Context context) {
        return TelephonyManagerTools.getMacList(context);
    }

    /**
     * 获取浏览器的useragent
     *
     * @param context
     * @return
     */
    @Override
    public String getUserAgent(Context context) {
        return DeviceConfig.getDefaultUserAgentString(context);
    }

    /**
     * 获取屏幕数据
     *
     * @param context
     * @return
     */
    @Override
    public DisplayMetrics getMetrics(Context context) {
        return DeviceUtils.getMetrics(context);
    }

}
