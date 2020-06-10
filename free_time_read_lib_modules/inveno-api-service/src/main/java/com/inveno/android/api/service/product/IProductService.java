package com.inveno.android.api.service.product;

public interface IProductService {
    String getProductId();

    String getPromotion();

    String createTk(String data, String time);

    String createTkWithoutData(String time);
}
