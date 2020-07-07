package com.inveno.xiandu.utils;

import android.util.Log;

/**
 * creat by: huzheng
 * date: 2019-09-17
 * description:
 * 日志打印
 */
public class LogUtils {

    private static final String TAG = "xiandu";
    public static final String TAG_ACCOUNT = "account";

    /**
     * 打印信息
     *
     * @param TAG 标签
     * @param tmp 内容
     */
    public static void showLog(String TAG, String tmp) {
            Log.i(TAG, "======" + tmp + "======");
    }

    public static void H(String tmp) {
        Log.i(TAG, "====== " + tmp + " ======");
    }

    public static void H(int tmp) {
        Log.i(TAG, "====== " + tmp + " ======");
    }

    public static void E(String tmp) {
        Log.e(TAG, "======" + tmp + "======");
    }


}
