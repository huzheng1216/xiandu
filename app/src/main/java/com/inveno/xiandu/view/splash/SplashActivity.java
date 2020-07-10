package com.inveno.xiandu.view.splash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.contract.listener.SplashAdListener;
import com.inveno.android.ad.contract.param.SplashAdParam;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.init.AppInitListener;
import com.inveno.xiandu.view.init.AppInitViewProxy;
import com.inveno.xiandu.view.main.my.ChoiseGenderActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created By huzheng
 * Date 2020-02-16
 * Des 启动页
 */
public class SplashActivity extends BaseActivity implements AppInitListener {

    private static final Logger logger = LoggerFactory.getLogger(SplashActivity.class);

    private AppInitViewProxy initViewProxy;

    private ViewGroup adContainerView;

    private final SplashAdListener mSplashAdListener = new SplashAdListener() {
        @Override
        public void onNoAD(String var1) {
            goToMain();
        }

        @Override
        public void onClicked() {

        }

        @Override
        public void onShow() {

        }

        @Override
        public void onPresent() {

        }

        @Override
        public void onADDismissed() {
            goToMain();
        }

        @Override
        public void extendExtra(String var1) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_splash);

        adContainerView = findViewById(R.id.ad_container_ll);

        initViewProxy = new AppInitViewProxy(this, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndInit();
            }
        }, 1500);
    }


    private void checkAndInit() {
//        if (initViewProxy.isNeedToCheckPermission()) {
        AppPermissionUtil.checkPermission(SplashActivity.this, new Runnable() {
            @Override
            public void run() {
                onAppPermissionGet();
            }
        }, true);

//        } else {
//            initViewProxy.init();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AppPermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        initViewProxy.init();
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void onAppPermissionGet() {
        initViewProxy.init();
    }

    private void readyToDo() {
        InvenoAdServiceHolder.getService().requestSplashAd(
                SplashAdParam.SplashAdParamBuilder.aSplashAdParam()
                        .withActivity(SplashActivity.this)
                        .withContainerView(adContainerView)
                        .withSplashAdListener(mSplashAdListener).build()
        ).onSuccess(new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                logger.info("requestSplashAd " + s);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer code, String msg) {
                logger.info("requestSplashAd fail code:" + code + " msg:" + msg);
                goToMain();
                return null;
            }
        }).execute();
    }

    private void goToMain() {
        //是否第一次启动
        boolean firstLaunch = SPUtils.getInformain(Keys.FIRST_LAUNCH_KEY, false);
        if (firstLaunch) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_MAIN)
                    .navigation();
            finish();
        } else {
            Intent intent = new Intent(this, ChoiseGenderActivity.class);
            intent.putExtra("request_code", ChoiseGenderActivity.MAIN_REQUEST_CODE);
            startActivityForResult(intent, ChoiseGenderActivity.MAIN_REQUEST_CODE);
            finish();
        }
    }

    @Override
    public void onAppInitSuccess() {
        readyToDo();
    }

    @Override
    public void onAppInitFail() {
        Toaster.showToast(this, "应用初始化失败");
        finish();
    }
}
