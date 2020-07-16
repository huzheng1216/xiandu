package com.inveno.xiandu.applocation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.basics.service.BasicsServiceModule;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.BuildConfig;
import com.inveno.xiandu.config.Const;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.crash.CrashHandler;
import com.inveno.xiandu.db.DaoManager;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.SPUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2016/9/23.
 */
public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context sInstance;
    private static MainApplication mInstance;

    private int activityCount;//activity的count数
    public static boolean isForeground;//是否在前台

    private static Typeface sanhansTypeface;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mInstance = this;
        Const.init();
        //初始化ARouter
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            ARouter.openLog();// 打印日志
            ARouter.openDebug();// 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        //初始化错误采集
        initCrash();
        //初始化微信分享
        initWeChat();
        ARouter.init(this);
        //初始化sp存储
        SPUtils.init(Keys.SP_KEY, this);
        //网络引擎
        DDManager.init(this);
        //数据库管理工具
        DaoManager.getInstance(this);
        //基础服务模块
        BasicsServiceModule.Companion.onApplicationCreate(this);
        InvenoServiceContext.init(this);
        //初始化宋体
//        initfonts();

        registerActivityLifecycleCallbacks(this);

        ReportManager.INSTANCE.init(BuildConfig.referrer, this ,BuildConfig.InvenoUrl );
    }

    public void initCrash() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

//    private void initfonts() {
//        AssetManager mmgr = getAssets();//得到AssetManager
//        sanhansTypeface = Typeface.createFromAsset(mmgr, "fonts/sourcehanserifcn_regular.otf");
//    }

//    public Typeface getSanhansTypeface() {
//        if (sanhansTypeface == null) {
//            initfonts();
//        }
//        return sanhansTypeface;
//    }

    public static Context getContext() {
        return sInstance;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
//        Log.i("ReportManager" , "===============onActivityCreated "+activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        ReportManager.INSTANCE.appStart();
        activityCount++;
//        Log.i("ReportManager" , "===============onActivityStarted "+activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
//        Log.i("ReportManager" , "===============onActivityResumed "+activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
//        Log.i("ReportManager" , "===============onActivityPaused "+activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityCount--;
        isForeground();
//        Log.i("ReportManager" , "===============onActivityStopped "+activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
//        Log.i("ReportManager" , "===============onActivitySaveInstanceState "+activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
//        Log.i("ReportManager" , "===============onActivityDestroyed "+activity);
    }


    /**
     * 判断是否在前台
     */
    private void isForeground() {
        if (activityCount > 0) {
            isForeground = true;
        } else {
            isForeground = false;
            ReportManager.INSTANCE.appEnd(this, ServiceContext.userService().getUserPid(), "");
        }
//        Log.e("ReportManager",+activityCount+"-------isForeground="+isForeground);
    }

    private void initWeChat(){
        // 三个参数分别是上下文、应用的appId、是否检查签名（默认为false）
        IWXAPI mWxApi = WXAPIFactory.createWXAPI(this, "你的appId", true);
// 注册
        mWxApi.registerApp("你的appId");
    }
}
