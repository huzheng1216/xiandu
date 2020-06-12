package com.inveno.xiandu.http.base;

import android.content.Context;

import com.inveno.xiandu.config.HttpConfig;
import com.inveno.xiandu.http.service.MRRequestService;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.SPUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Describe: 基础业务类（组装基础请求参数）
 * Created by ${zheng.hu} on 2016/9/26.
 */
public class BaseBiz {

    protected MRRequestService mMRRequestService;

    public BaseBiz(final Context context) {

//        //日志显示级别
//        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
//        //新建log拦截器
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                LogTools.showLog("zheng.http", "请求url : " + message);
//            }
//        });
//        loggingInterceptor.setLevel(level);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //处理请求
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Accept-Language", "zh-CN")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Cookie", SPUtils.getInformain("nxck", ""))
                                .addHeader("Cookie", "uid=000000")
                                .addHeader("Accept", "*/*")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        //这里获取请求返回的cookie
                        if (!response.headers("Set-Cookie").isEmpty()) {
                            Observable.fromIterable(response.headers("Set-Cookie"))
                                    .map(new Function<String, String>() {
                                        @Override
                                        public String apply(String s) {
                                            String[] cookieArray = s.split(";");
                                            for (String str : cookieArray) {
                                                if (str.indexOf("cuid") >= 0) {
                                                    return str;
                                                }
                                            }
                                            return "";
                                        }
                                    })
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String cookie) throws Exception {
                                            LogUtils.showLog(LogUtils.TAG_ACCOUNT, "获取到的cookie = " + cookie);
                                            if (cookie.indexOf("cuid") >= 0) {
                                                LogUtils.showLog(LogUtils.TAG_ACCOUNT, "保存获取到的cookie");
                                                SPUtils.setInformain("nxck", cookie);
                                                String a[] = cookie.split("=");
                                                SPUtils.setInformain("cuid", a[1]);
                                            }
                                        }
                                    });
                        }

                        return response;
                    }
                })
                .addInterceptor(new LoggingInterceptor());
        builder.connectTimeout(15, TimeUnit.SECONDS);
        Retrofit retorfit = new Retrofit.Builder()
                .baseUrl(HttpConfig.HOST)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mMRRequestService = retorfit.create(MRRequestService.class);
    }

    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

//            long t1 = System.nanoTime();
            LogUtils.showLog("zheng.http", "请求url : " + request.url());
//            Logger.d(String.format("Sending request %s on %s%n%s",
//                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

//            long t2 = System.nanoTime();
//            LogTools.showLog("zheng.http", "返回 : " + String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//            Logger.d(String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));


            final String responseString = new String(response.body().bytes());

            LogUtils.showLog("zheng.http", "返回 : " + responseString);
//            Logger.d("Response: " + responseString);

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), responseString))
                    .build();
        }
    }
}
