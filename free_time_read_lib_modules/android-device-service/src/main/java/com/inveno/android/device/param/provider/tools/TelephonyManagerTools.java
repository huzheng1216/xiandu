package com.inveno.android.device.param.provider.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.inveno.android.device.param.provider.bean.GsmBean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 获取手机SIM卡信息的工具类 需要权限：android.permission.READ_PHONE_STATE
 *
 * @author mingsong.zhang
 * @date 20120627
 */
public class TelephonyManagerTools {

    /**
     * 唯一的设备ID：<br/>
     * 如果是GSM网络，返回IMEI；如果是CDMA网络，返回MEID<br/>
     * 需要权限：android.permission.READ_PHONE_STATE
     *
     * @return null if device ID is not available.
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImei(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            assert manager != null;
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei1 = (String) method.invoke(manager, 0);
            String imei2 = (String) method.invoke(manager, 1);
            if (TextUtils.isEmpty(imei2)) {
                return imei1;
            }
            if (!TextUtils.isEmpty(imei1)) {
                //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                String imei = "";
                if (imei1.compareTo(imei2) <= 0) {
                    imei = imei1;
                } else {
                    imei = imei2;
                }
                return imei;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return manager.getDeviceId();
            }catch (Exception e2){
                e2.printStackTrace();
                return "";
            }
        }
        return "";
    }

    private static boolean isValidImei(String imei) {
        return imei != null && imei.length() >= 14;
    }

    /**
     * 获取mac地址 需要权限：android.permission.READ_PHONE_STATE
     *
     * @return null if device ID is not available.
     */
    public static String getMacAddress(Context context) {
        if (context == null) {
            return "";
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return getMacDefault(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getMacAddress();
        } else {
            return getMacFromHardware();
        }
    }

    /**
     * 获取所有连接的wifi的mac地址
     *
     * @param context
     * @return
     */
    public static List<String> getMacList(Context context) {
        return MacAddressUtil.getMacList(context);
    }

    /**
     * 防止某些手机拿到的是默认的mac地址导致产生了某些问题
     *
     * @return
     */
    @SuppressLint("NewApi")
    private static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     *
     * @param context * @return
     */
    public static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    public static String getMacAddress() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();//去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        return macSerial;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 检查手机是否有sim卡
     */
    private boolean hasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            return false;
        }
        return true;
    }

    /**
     * 获取运营商信息
     *
     * @return null if device ID is not available.
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getProvidersName(Context context) {
        if (context == null) {
            return "";
        }
        String providersName = "";
        if (TextUtils.isEmpty(providersName)) {
            TelephonyManager telMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            // 返回唯一的用户ID;就是这张卡的编号神马的
            String IMSI = null;
            try {
                assert telMgr != null;
                IMSI = telMgr.getSubscriberId();
            } catch (Exception e) {
                IMSI = "";
            }


            LogTools.showLog("op", "imsi:" + IMSI);
            // 可能为null
            if (!TextUtils.isEmpty(IMSI)) {
                // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
                        || IMSI.startsWith("46007")) {
                    providersName = "CMCC";
                } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                    // 联通
                    providersName = "CUCC";
                } else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
                    // 电信
                    providersName = "CTCC";
                }
            }

            return providersName;

        }
        return providersName;
    }

    /**
     * 获取mac地址
     *
     * @return get mac address.
     */
    @SuppressLint("HardwareIds")
    public static String getMac(Context context) {

        // tencent过滤了获取服务WIFI_SERVICE，必须获取app的context来获取
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext().getSystemService(
                        Context.WIFI_SERVICE);

        String mac = null;
        if (wifiManager != null) {
            mac = wifiManager.getConnectionInfo().getMacAddress();
        }

        return mac;
    }


    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String GetNetworkType(Context context) {
        String strNetworkType = "";

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_NR:
                        strNetworkType = "5G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }


        return strNetworkType;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImsi(Context context) {
        /** 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        if (context == null) {
            return "";
        }
        String imsi = "";
        if (TextUtils.isEmpty(imsi)) {
            TelephonyManager telMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                assert telMgr != null;
                imsi = telMgr.getSubscriberId();
            } catch (Exception e) {
                imsi = "";
            }
        }
        return imsi;
    }

    private static GsmBean gsmBean;
    public static GsmBean getGsmLocation(Context context) {
        TelephonyManager mTelMan = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (gsmBean!=null)
            return gsmBean;
        gsmBean = new GsmBean();
        try {
            String operator = mTelMan.getNetworkOperator();
            gsmBean.setNmcc(operator.substring(0, 3));
            gsmBean.setNmnc(operator.substring(3));
            @SuppressLint("MissingPermission") CellLocation location = mTelMan.getCellLocation();
            if (location instanceof GsmCellLocation) {
                gsmBean.setCell_id(((GsmCellLocation) location).getCid());
                gsmBean.setLac(((GsmCellLocation) location).getLac());
            } else if (location instanceof CdmaCellLocation) {
                gsmBean.setLac(((CdmaCellLocation) location).getNetworkId());
                gsmBean.setCell_id(((CdmaCellLocation) location).getBaseStationId());
            }
        } catch (Exception e) {
            gsmBean.setNmcc("");
            gsmBean.setNmnc("");
            gsmBean.setLac(0);
            gsmBean.setCell_id(0);
        }
        return gsmBean;
    }
}
