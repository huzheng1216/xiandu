package com.inveno.android.device.param.provider.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.inveno.android.device.param.provider.IAppParamProvider;

public class AppParamProvider implements IAppParamProvider {

    private String versionName;

    public AppParamProvider(Context context) {
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersionName() {
        return versionName;
    }
}
