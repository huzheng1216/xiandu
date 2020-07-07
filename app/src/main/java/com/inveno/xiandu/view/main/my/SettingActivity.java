package com.inveno.xiandu.view.main.my;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.android.basics.service.event.EventService;
import com.inveno.xiandu.R;
import com.inveno.xiandu.apkupdata.UpdateApkManager;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.api.user.LoginAPI;
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.AppInfoUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.dialog.IosTypeDialog;

/**
 * @author yongji.wang
 * @date 2020/6/9 17:12
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_SETTING)
public class SettingActivity extends TitleBarBaseActivity {

    private TextView mine_push_status;

    private TextView mine_appversion;

    private IosTypeDialog iosTypeDialog;

    private TextView logout;

    private boolean areNotificationEnable = false;

    @Override
    public String getCenterText() {
        return "设置";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_mine_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    protected void initView() {
        super.initView();
        mine_push_status = findViewById(R.id.mine_push_status);

        mine_appversion = findViewById(R.id.mine_appversion);

        mine_appversion.setText(AppInfoUtils.getAppVersion(this));

        logout = findViewById(R.id.logout);
        if (!ServiceContext.userService().isLogin()) {
            logout.setVisibility(View.GONE);
        } else {
            logout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        areNotificationEnable = getPushNotification();
        if (areNotificationEnable) {
            mine_push_status.setText("开");
        } else {
            mine_push_status.setText("关");
        }
    }

    public boolean getPushNotification() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    public void jumpToNotificationSetting() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
            startActivity(intent);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    //在dialog.show()之后调用
    public void setDialogWindowAttr(Dialog dlg) {
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.8); //设置宽度
        dlg.getWindow().setAttributes(lp);
    }

    public void user_data(View view) {
        Intent intent = new Intent(SettingActivity.this, UserinfoActivity.class);
        startActivity(intent);
//        Toaster.showToastCenter(this, "用户信息");
    }

    public void push_setting(View view) {
//        Toaster.showToastCenter(this, "推送设置");
        if (areNotificationEnable) {
            //跳转到通知开关页面
            IosTypeDialog.Builder builder = new IosTypeDialog.Builder(this);
            builder.setContext("请在系统设置-通知中心里关闭通知开关");
            builder.setTitle("提示");
            builder.setRightButton("知道了", new IosTypeDialog.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosTypeDialog.dismiss();
                    iosTypeDialog = null;
                }
            });

            iosTypeDialog = builder.create();
            iosTypeDialog.show();
            setDialogWindowAttr(iosTypeDialog);
        } else {
            //跳转到通知开关页面
            IosTypeDialog.Builder builder = new IosTypeDialog.Builder(this);
            builder.setContext("请在系统设置里打开通知开关");
            builder.setTitle("提示");
            builder.setLeftButton("取消", new IosTypeDialog.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosTypeDialog.dismiss();
                }
            });
            builder.setRightButton("去设置", new IosTypeDialog.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosTypeDialog.dismiss();
                    iosTypeDialog = null;
                    jumpToNotificationSetting();
                }
            });

            iosTypeDialog = builder.create();

            iosTypeDialog.show();
            setDialogWindowAttr(iosTypeDialog);
        }
    }

    public void get_version(View view) {
        updata();
//        Toaster.showToastCenter(this, "检查更新");

    }


    private void updata() {
        //应用更新提示
        UpdateApkManager updateApkManager = new UpdateApkManager.Builder(this).updateListener(new UpdateApkManager
                .UpdateListener() {
            @Override
            public void acceptUpdate(String msg) {

            }

            @Override
            public void rejectUpdate(String errorMsg) {

            }
        });
        updateApkManager.setSettingCheck(true);
        updateApkManager.update();
    }

    public void logout(View view) {
        IosTypeDialog.Builder builder = new IosTypeDialog.Builder(this);
        builder.setContext("确认要退出当前帐号吗？");
        builder.setTitle("提示");
        builder.setLeftButton("退出", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPersistRepository.get().save(LoginAPI.USER_DATA_KEY, "");
                ServiceContext.userService().setUserInfo(null);

                EventService.Companion.post(EventConstant.LOGOUT);
                finish();
            }
        });
        builder.setRightButton("取消", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                iosTypeDialog.dismiss();
                iosTypeDialog = null;
            }
        });

        iosTypeDialog = builder.create();
        iosTypeDialog.show();
        setDialogWindowAttr(iosTypeDialog);
    }

}
