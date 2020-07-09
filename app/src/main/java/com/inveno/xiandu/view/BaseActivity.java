package com.inveno.xiandu.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.ActivityManager;
import com.inveno.xiandu.view.detail.BookDetailActivity;
import com.inveno.xiandu.view.event.EventNetChange;
import com.inveno.xiandu.view.event.EventNightModeChange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author huzheng
 * @date 2019-09-17
 * @description 基础功能组件
 */
public abstract class BaseActivity extends AppCompatActivity {

    //1 服务器错误，2 加载失败，3没有书，4，网络错误
    public static final int SERVICE_ERROR = 1;
    public static final int LOAD_ERROR = 2;
    public static final int NO_BOOK_ERROR = 3;
    public static final int NETWORK_ERROR = 4;
    //权限列表
    public final static String[] PERMS_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        translucentStatusBar(this, true);
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
        //将Activity实例添加到AppManager的堆栈
        ActivityManager.getAppManager().addActivity(this);
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
        ActivityManager.getAppManager().finishActivity(this);
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

    //去掉半透明状态栏
    protected void setStatusBar() {
        //7.0以上才做操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                View view = getWindow().getDecorView();
//                @SuppressLint("PrivateApi") Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = view.getClass().getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
                Log.i("setStatusBar", "setStatusBar: " + e.getMessage());
                e.printStackTrace();
            }
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
        //透明式状态栏
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
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
        translucentStatusBar(this, false);

        setStatusBarBlack();
    }

    void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (hideStatusBarBackground) {
                //如果为全透明模式，取消设置Window半透明的Flag
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //设置状态栏为透明
                window.setStatusBarColor(Color.TRANSPARENT);

                //设置window的状态栏不可见
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    protected void setStatusBarBlack() {
        //实现状态栏图标和文字颜色为暗色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, 0);

    }

    protected void setStatusBarLight() {
        //实现状态栏图标和文字颜色为浅色
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, 0);
    }

    private PopupWindow errorPopup;

    public void showErrorPopwindow(int errorType, OnClickListener listener) {
        View view;
        if (errorType == SERVICE_ERROR) {
            view = getServiceErrorView(listener);
        } else if (errorType == LOAD_ERROR) {
            view = getLoadErrorView(listener);
        } else if (errorType == NO_BOOK_ERROR) {
            view = getnoBookErrorView(listener);
        } else if (errorType == NETWORK_ERROR) {
            view = getnetworkErrorView(listener);
        } else {
            view = getnetworkErrorView(listener);
        }
        errorPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, calWidthAndHeight(this));
        errorPopup.setClippingEnabled(false);//全屏显示
        errorPopup.setFocusable(true);// 取得焦点
        //点击外部消失
        errorPopup.setOutsideTouchable(false);
        //设置可以点击
        errorPopup.setTouchable(true);
        errorPopup.setHeight(calWidthAndHeight(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(errorPopup, true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 按下android回退物理键 PopipWindow消失解决
        //从底部显示
        errorPopup.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     */
    private int calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return getHasVirtualKey();
    }
    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     *
     * @return
     */
    private int getHasVirtualKey() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    private View getServiceErrorView(OnClickListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_service_error_view, null);
        TextView error_refresh = contentView.findViewById(R.id.error_refresh);
        TextView error_back = contentView.findViewById(R.id.error_back);
        error_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshClick();
            }
        });
        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorPopup.dismiss();
                listener.onBackClick();
            }
        });
        return contentView;
    }

    private View getLoadErrorView(OnClickListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_load_error_view, null);
        TextView error_refresh = contentView.findViewById(R.id.error_refresh);
        TextView error_back = contentView.findViewById(R.id.error_back);
        error_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshClick();
            }
        });
        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorPopup.dismiss();
                listener.onBackClick();
            }
        });
        return contentView;
    }

    private View getnoBookErrorView(OnClickListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_not_book_error_view, null);
        TextView error_back = contentView.findViewById(R.id.error_back);
        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorPopup.dismiss();
                listener.onBackClick();
            }
        });
        return contentView;
    }

    private View getnetworkErrorView(OnClickListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_network_error_view, null);
        TextView error_refresh = contentView.findViewById(R.id.error_refresh);
        TextView error_back = contentView.findViewById(R.id.error_back);
        error_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshClick();
            }
        });
        error_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorPopup.dismiss();
                listener.onBackClick();
            }
        });
        return contentView;
    }

    public interface OnClickListener {
        void onBackClick();

        void onRefreshClick();
    }
}
