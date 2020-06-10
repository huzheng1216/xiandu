package com.inveno.android.api.api;

import com.inveno.android.api.api.ad.AdAPI;
import com.inveno.android.api.api.uid.UidAPI;
import com.inveno.android.basics.service.app.context.InstanceContext;

public class InvenoAPIContext {
    public static UidAPI uid() {
        return InstanceContext.get().getInstance(UidAPI.class);
    }
    public static AdAPI ad() {
        return InstanceContext.get().getInstance(AdAPI.class);
    }
}
