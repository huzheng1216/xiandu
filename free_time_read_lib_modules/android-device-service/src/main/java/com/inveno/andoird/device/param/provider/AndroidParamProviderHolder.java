package com.inveno.andoird.device.param.provider;

import android.content.Context;

import com.inveno.andoird.device.param.provider.impl.AndroidParamProvider;

public class AndroidParamProviderHolder {

    private static IAndroidParamProvider sAndroidParamProvider;

    public static void install(Context applicationContext) {
        sAndroidParamProvider = new AndroidParamProvider(applicationContext);
    }

    public static IAndroidParamProvider get() {
        return sAndroidParamProvider;
    }
}
