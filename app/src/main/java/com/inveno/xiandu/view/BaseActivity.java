package com.inveno.xiandu.view;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.view.detail.BookDetailActivity;
import com.inveno.xiandu.view.event.EventNetChange;
import com.inveno.xiandu.view.event.EventNightModeChange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

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
        //透明式状态栏
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
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
        errorPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        errorPopup.setFocusable(true);// 取得焦点
        //点击外部消失
        errorPopup.setOutsideTouchable(false);
        //设置可以点击
        errorPopup.setTouchable(true);

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
//        errorPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void getNetWork() {

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
