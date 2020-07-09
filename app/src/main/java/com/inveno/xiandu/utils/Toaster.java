package com.inveno.xiandu.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {

    private static Toast toast;
    private static Toast toastCenter;

    public static void showToast(Context context, String str) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
            }
            toast.setText(str);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastShort(Context context, String str) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            }
            toast.setText(str);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastCenter(Context context, String str) {

        if (toastCenter != null) {
            toastCenter.cancel();
        }
        toastCenter = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toastCenter.setGravity(Gravity.CENTER, 0, 0);
        toastCenter.setText(str);
        toastCenter.show();
    }

    public static void showToastCenterShort(Context context, String str) {

        if (toastCenter != null) {
            toastCenter.cancel();
        }
        toastCenter = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toastCenter.setGravity(Gravity.CENTER, 0, 0);
        toastCenter.setText(str);
        toastCenter.show();
    }

    public static void showNetWorkErrorCenterShort(Context context, int error_code, String msg) {
        if (error_code == 503) {
            showToastCenterShort(context, "服务发生错误");
        } else if (error_code == 403) {
            showToastCenterShort(context, "无访问权限");

        } else if (error_code == 404) {
            showToastCenterShort(context, "请求数据不存在");

        } else if (error_code == 1001) {
            showToastCenterShort(context, "内容包含敏感信息");

        } else if (error_code == 1002) {
            showToastCenterShort(context, "服务器繁忙");

        } else if (error_code == 1003) {
            showToastCenterShort(context, "内容已下线或当前无法访问");

        } else if (error_code == 1004) {
            showToastCenterShort(context, "服务发生错误");
        } else {
            showToastCenterShort(context, msg);
        }
    }

    public static void showNetWorkErrorShort(Context context, int error_code, String msg) {
        if (error_code == 503) {
            showToastShort(context, "服务发生错误");
        } else if (error_code == 403) {
            showToastShort(context, "无访问权限");

        } else if (error_code == 404) {
            showToastShort(context, "请求数据不存在");

        } else if (error_code == 1001) {
            showToastShort(context, "内容包含敏感信息");

        } else if (error_code == 1002) {
            showToastShort(context, "服务器繁忙");

        } else if (error_code == 1003) {
            showToastShort(context, "内容已下线或当前无法访问");

        } else if (error_code == 1004) {
            showToastShort(context, "服务发生错误");
        } else {
            showToastShort(context, msg);
        }
    }
}
