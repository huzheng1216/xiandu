package com.inveno.andoird.api;

import com.inveno.andoird.api.uid.UidAPI;
import com.inveno.android.basics.service.app.context.InstanceContext;

public class InvenoAPIContext {
    public static UidAPI uid() {
        return InstanceContext.get().getInstance(UidAPI.class);
    }
}
