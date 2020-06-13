package com.inveno.android.device.param.provider.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.inveno.android.device.param.provider.IAppParamProvider;

public class AppParamProvider implements IAppParamProvider {

    private String versionName;
    private Long versionCode;

    public AppParamProvider(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionCode = Long.valueOf(packageInfo.versionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionCode = packageInfo.getLongVersionCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersionName() {
        return versionName;
    }

    @Override
    public Long getVersionCode() {
        return versionCode;
    }
}
