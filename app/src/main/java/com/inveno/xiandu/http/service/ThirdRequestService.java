package com.inveno.xiandu.http.service;


import com.inveno.xiandu.http.callback.RequestCallBack;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 请求第三方服务接口
 * Created by Administrator on 2016/9/23.
 */
public interface ThirdRequestService {
    @GET
    Observable<RequestCallBack> synYueWenPay();
}
