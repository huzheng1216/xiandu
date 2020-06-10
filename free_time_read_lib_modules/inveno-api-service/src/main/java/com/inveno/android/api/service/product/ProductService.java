package com.inveno.android.api.service.product;

import com.google.common.hash.Hashing;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;

import java.nio.charset.StandardCharsets;

public class ProductService extends BaseSingleInstanceService implements IProductService {
    private String productId = "xianduxiaoshuo";
    private String promotion = "xiandu";
    private String appKey = "fe8c52d18ac169d720d7a37c5da7e2dc";
    private String appSecret = "e3dccdbeeefeb574b3ad7ae5df1a2cf34b7aeabb";


    public String getProductId(){
        return productId;
    }

    public String getPromotion(){
        return promotion;
    }

    public String createTk(String data,String time){
        //  md5(APPSECRET+":"+$data+":"+$tm)
        return Hashing.md5().newHasher().putString(appSecret+":"+data+":"+time, StandardCharsets.UTF_8).hash().toString();
    }

    public String createTkWithoutData(String time){
        //  md5(APPSECRET+":"+$data+":"+$tm)
        return createTk("",time);
    }
}
