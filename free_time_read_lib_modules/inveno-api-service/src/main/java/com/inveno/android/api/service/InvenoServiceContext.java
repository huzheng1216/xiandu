package com.inveno.android.api.service;

import android.content.Context;

import com.inveno.android.api.service.ad.AdService;
import com.inveno.android.api.service.product.IProductService;
import com.inveno.android.api.service.product.ProductService;
import com.inveno.android.api.service.uid.UidService;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.android.basics.service.app.context.InstanceContext;

public class InvenoServiceContext {

    public static void init(Context applicationContext) {
        AndroidParamProviderHolder.install(applicationContext);
    }

    public static IProductService product() {
        return InstanceContext.get().getInstance(ProductService.class);
    }

    public static UidService uid(){
        return InstanceContext.get().getInstance(UidService.class);
    }

    public static AdService ad(){
        return InstanceContext.get().getInstance(AdService.class);
    }
}
