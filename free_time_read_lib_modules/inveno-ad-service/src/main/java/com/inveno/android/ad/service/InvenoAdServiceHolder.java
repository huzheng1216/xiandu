package com.inveno.android.ad.service;

public class InvenoAdServiceHolder {

    private static final InvenoAdService sInstance = new InvenoAdService();

    public static InvenoAdService getService() {
        return sInstance;
    }
}
