package com.inveno.android.device.param.provider.impl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.inveno.android.device.param.provider.IDeviceParamProvider;
import com.inveno.android.device.param.provider.tools.DeviceConfig;
import com.inveno.android.device.param.provider.tools.DeviceUtils;
import com.inveno.android.device.param.provider.tools.TelephonyManagerTools;

import java.util.List;

/**
 * 设备参数实现类
 *
 * @author yongji.wang
 * @date 2020/6/4 14:57
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 *
 * @log Context 不需要每个请求都传，放在构造函数即可 @杨云龙
 */
public class DeviceParamProvider implements IDeviceParamProvider {

    private Context context;

    public DeviceParamProvider(Context context) {
        this.context = context;
    }

    /**
     * 获取平台
     *
     * @return
     */
    @Override
    public String getPlatform() {
        return "android";
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    @Override
    public String getNetwork() {
//        network	说明
//        1	wifi网络
//        2	2G 移动网络
//        3	3G 移动网络
//        4	4G 移动网络
//        5	5G 移动网络
//        6	其他移动网络
//        7	其他网络（非wifi并且也非移动网络）
//        8	unknown，纯H5产品，获取不了网络状况
        String networkType = TelephonyManagerTools.GetNetworkType(context);
        String networkNum = "7";
        if (networkType.equals("WIFI")){
            networkNum = "1";
        }else if (networkType.equals("2G")){
            networkNum = "2";
        }else if (networkType.equals("3G")){
            networkNum = "3";
        }else if (networkType.equals("4G")){
            networkNum = "4";
        }else if (networkType.equals("5G")){
            networkNum = "5";
        }
        return networkNum;
    }

    /**
     * 获取imei
     *
     * @return
     */
    @Override
    public String getImei() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return "";
        }else{
            return TelephonyManagerTools.getImei(context);
        }
    }

    @Override
    public String getAid() {
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
    public String getMcc() {
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
    public String getMnc() {
        if (TextUtils.isEmpty(TelephonyManagerTools.getImsi(context))) {
            return "";
        }
        return TelephonyManagerTools.getImsi(context).substring(3, 5);
    }

    @Override
    public int getCell_id() {
        return TelephonyManagerTools.getGsmLocation(context).getCell_id();
    }

    @Override
    public int getLac() {
        return TelephonyManagerTools.getGsmLocation(context).getLac();
    }

    /**
     * 获取当前网络移动运营商国家代码
     *
     * @return
     */
    @Override
    public String getNmcc() {
        return TelephonyManagerTools.getGsmLocation(context).getNmcc();
    }

    /**
     * 获取当前网络移动运营商网络代码
     *
     * @return
     */
    @Override
    public String getNmnc() {
        return TelephonyManagerTools.getGsmLocation(context).getNmnc();
    }

    /**
     * 扫描到的WIFI的MAC地址
     *
     * @return
     */
    @Override
    public String getMac() {
        return TelephonyManagerTools.getMacAddress(context);
    }

    /**
     * 扫描到的WIFI的Mac地址列表的集合
     *
     * @return
     */
    @Override
    public List<String> getWiFiMacList() {
        return TelephonyManagerTools.getMacList(context);
    }

    /**
     * 获取浏览器的useragent
     *
     * @return
     */
    @Override
    public String getUserAgent() {
        return DeviceConfig.getDefaultUserAgentString(context);
    }

    /**
     * 获取屏幕数据
     *
     * @return
     */
    @Override
    public DisplayMetrics getMetrics() {
        return DeviceUtils.getMetrics(context);
    }

    @Override
    public String getOperator() {
        return "CMCC";
    }

}
