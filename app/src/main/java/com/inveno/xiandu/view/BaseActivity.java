package com.inveno.xiandu.view;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.view.event.EventNetChange;
import com.inveno.xiandu.view.event.EventNightModeChange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author huzheng
 * @date 2019-09-17
 * @description 基础功能组件
 */
public abstract class BaseActivity extends AppCompatActivity {

    //权限列表
    public final static String[] PERMS_WRITE ={Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //监听黑夜白天模式
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayNightChange(EventNightModeChange eventNightModeChange) {
    }

    //监听网络切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayNightChange(EventNetChange eventNetChange) {
    }

    protected void requestMyPermissions() {
        if (!EasyPermissions.hasPermissions(this, PERMS_WRITE)) {
//            EasyPermissions.requestPermissions(this, "漫画狗将使用本地存储权限，用户存储漫画数据以及");
        }
    }

    /**
     * @author huzheng
     * @date 2019-09-17
     * @description 设置状态栏颜色
     * android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
     * 如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
     */
    protected void setStatusBar(int color, boolean isResource) {
        int mColor = isResource ? getResources().getColor(color) : color;
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //根据上面设置是否对状态栏单独设置颜色
            getWindow().setStatusBarColor(mColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        //android6.0以后可以对状态栏文字颜色和图标进行修改
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断颜色是否为：亮色
            boolean isDark = ColorUtils.calculateLuminance(mColor) >= 0.5;
            if (isDark) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
}
