package com.inveno.android.device.param.provider.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

/**
 * 手机硬件参数数据类，欢迎界面获取手机硬件参数存入SP中 便于其他地方使用以及定时做上传
 *
 * @author blueming.wu
 * @date 2012-10-22
 */
public class DeviceConfig {

    /**
     * 手机屏幕宽高（分辨率）
     */
    private static int w;
    private static int h;
    public static String imei;
    public static String imsi;
    public static String mac;
    public static String mac_list;
    public static int  mac_list_max_size = 20;
    public static String net = "WIFI";
    public static String operator;
    public static String aid;
    public static String country;

    public static int StatusBarHeight;

    public static float density;
    public static float densityDPI;



    public static String ua;

    public static int LAC = 0;
    public static int CELL_ID = 0;
    public static String MNC;
    public static String MCC;
    public static int type;

    /**
     * 得到屏幕尺寸数据存入DeviceConfig
     */
    public static void initScreenSize(Context context) {
        if (w == 0 || h == 0) {
            LogTools.showLogB("手机分辨率w：" + w + "  h:" + h);
            reinstallScreenSize(context);
        }
    }

    /**
     * 重新得到屏幕尺寸数据存入DeviceConfig
     */
    public static void reinstallScreenSize(Context context) {
        DisplayMetrics size = new DisplayMetrics();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(size);
        density = size.density;
        densityDPI = size.densityDpi;
        w = size.widthPixels;
        h = size.heightPixels;
        LogTools.showLogB("reinstallScreenSize 手机分辨率w：" + w + "  h:" + h);
    }

    /**
     * 判断是否为平板
     *
     * @return
     */
    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于8尺寸则为Pad
        if (screenInches >= 8.0) {
            return true;
        }
        return false;
    }

    public static String getOsLocaleLanguage(Context context) {
        String lang = "";
        Locale l = context.getResources().getConfiguration().locale;
        if (l != null) {
            lang = l.getLanguage() + "_" + l.getCountry();
        }
        return lang;
    }

    public static int getDeviceWidth() {
        return w;
    }


    public static int getDeviceHeight() {
        return h;
    }


    public static String getCountry() {
        return country;
    }

    /**
     * 网络发生变化，重新获取网络状态
     *
     * @return void 返回类型
     * @throws
     * @Title: resetNetStatus
     */
    public static void resetNetStatus(Context context) {
        int netInt = NetWorkUtil.getNetWorkType(context);
        switch (netInt) {
            case NetWorkUtil.NETWORKTYPE_2G:
                net = "2G";
                break;
            case NetWorkUtil.NETWORKTYPE_3G:
                net = "3G";
                break;
            case NetWorkUtil.NETWORKTYPE_WIFI:
                net = "WIFI";
                break;
        }
        LogTools.showLogB("网络发生变化，重新获取：" + net);
    }

    /**
     * @param @param context 设定文件
     * @return void 返回类型
     * @throws
     * @Title: initDeviceData
     * @Description: 初始化设备基础数据
     */
    public static void initDeviceData(Context context) {
        if (TextUtils.isEmpty(imei))
            imei = TelephonyManagerTools.getImei(context);
        if (TextUtils.isEmpty(imsi))
            imsi = TelephonyManagerTools.getImsi(context);
        if (TextUtils.isEmpty(mac))
            mac = TelephonyManagerTools.getMacAddress(context);
        if (TextUtils.isEmpty(mac_list)){
            StringBuilder stringBuilder = new StringBuilder();
            List<String> macList = TelephonyManagerTools.getMacList(context);
            for(int i=0,size = macList.size();i<size && i< mac_list_max_size;i++ ){
                stringBuilder.append(macList.get(i)).append(",");
            }
            if(stringBuilder.length()>0){
                stringBuilder.setLength(stringBuilder.length()-1);
            }
            mac_list = stringBuilder.toString();
        }
        if (TextUtils.isEmpty(operator))
            operator = TelephonyManagerTools.getProvidersName(context);
        if (TextUtils.isEmpty(aid)) {
            aid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if (TextUtils.isEmpty(MNC)) {
            getGsmLocation(context);
        }
        if (TextUtils.isEmpty(net)) {
            int netInt = NetWorkUtil.getNetWorkType(context);
            switch (netInt) {
                case NetWorkUtil.NETWORKTYPE_2G:
                    net = "2G";
                    break;
                case NetWorkUtil.NETWORKTYPE_3G:
                    net = "3G";
                    break;
                case NetWorkUtil.NETWORKTYPE_WIFI:
                    net = "WIFI";
                    break;
            }
            LogTools.showLogB("获取网络：" + net);
        }
        try {
            type = NetWorkUtil.getNewNetWorkType(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(ua)) {
            ua = getDefaultUserAgentString(context);
            LogTools.showLogB("获取user-agent：" + ua);
        }
        if (StatusBarHeight == 0)
            StatusBarHeight = getStatusBarHeight(context);

        if (TextUtils.isEmpty(country)) {
            country = getOsLocaleLanguage(context);
            LogTools.showLog("deviceinfo", "country:" + country);
        }
    }

    // 获取手机状态栏高度
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }



    /**
     * 获取系统浏览器默认user-agent
     *
     * @param context
     * @return
     */
    public static String getDefaultUserAgentString(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            return NewApiWrapper.getDefaultUserAgent(context);
        }

        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                return settings.getUserAgentString();
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            return System.getProperty("http.agent");
        }
    }

    @TargetApi(17)
    static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            try {
                return WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static void getGsmLocation(Context context) {
        TelephonyManager mTelMan = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String operator = mTelMan.getNetworkOperator();
            MCC = operator.substring(0, 3);
            MNC = operator.substring(3);
            @SuppressLint("MissingPermission") CellLocation location = mTelMan.getCellLocation();
            if (location instanceof GsmCellLocation) {
                CELL_ID = ((GsmCellLocation) location).getCid();
                LAC = ((GsmCellLocation) location).getLac();
            } else if (location instanceof CdmaCellLocation) {
                LAC = ((CdmaCellLocation) location).getNetworkId();
                CELL_ID = ((CdmaCellLocation) location).getBaseStationId();
            }
        } catch (Exception e) {
            MCC = "";
            MNC = "";
            CELL_ID = 0;
            LAC = 0;
            Log.e("inveno", "getGsmLocation error:" + e.getMessage());
        }

    }

}
