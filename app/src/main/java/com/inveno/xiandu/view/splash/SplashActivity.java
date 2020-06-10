package com.inveno.xiandu.view.splash;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.init.AppInitListener;
import com.inveno.xiandu.view.init.AppInitViewProxy;

/**
 * Created By huzheng
 * Date 2020-02-16
 * Des 启动页
 */
public class SplashActivity extends BaseActivity implements AppInitListener {

    private ImageView ad;
    private TextView skip;
    private Dialog dialog;
    private AppInitViewProxy initViewProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_splash);

        ad = findViewById(R.id.iv_splash_ad);
        skip = findViewById(R.id.bt_splash_skip);

        //是否第一次启动
        boolean firstLaunch = SPUtils.getInformain(Keys.FIRST_LAUNCH_KEY, false, this);

        initViewProxy = new AppInitViewProxy(this, this);

        if (!firstLaunch) {
            //用户协议
            dialogShow2();
        } else {
            //广告时间 TODO
            checkAndInit();
        }
    }



    private void checkAndInit(){
        if(initViewProxy.isNeedToCheckPermission()){
            AppPermissionUtil.checkPermission(SplashActivity.this, new Runnable() {
                @Override
                public void run() {
                    onAppPermissionGet();
                }
            },true);

        }else{
            initViewProxy.init();
        }
    }

    //显示协议界面
    private void dialogShow2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_splash_agreement, null);
        TextView content = (TextView) v.findViewById(R.id.bt_splash_agreement_content);
        content.setText(getClickableSpan());
        //设置该句使文本的超连接起作用
        content.setMovementMethod(LinkMovementMethod.getInstance());
        View btn_sure = v.findViewById(R.id.bt_splash_agreement_ok);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        ClickUtil.bindSingleClick(btn_sure, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.setInformain(Keys.FIRST_LAUNCH_KEY, true, SplashActivity.this);
                checkAndInit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AppPermissionUtil.onRequestPermissionsResult(this,requestCode, permissions, grantResults);
    }

    private void onAppPermissionGet(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        initViewProxy.init();
    }

    private void readyToGoMain(){
        ARouter.getInstance().build(ARouterPath.ACTIVITY_MAIN)
                .navigation();
        finish();
    }

    //设置超链接文字
    private SpannableString getClickableSpan() {
        SpannableString spanStr = new SpannableString("欢迎使用漫画狗搜索，在你开始使用我们的产品及服务前，请充分阅读并理解《用户协议》和《隐私政策》中的相关条款：\n" +
                "1.在仅浏览时，为保障服务所需，我们会申请系统权限收集设备信息和日志信息用于内容推送；\n" +
                "2.我们会申请存储权限，用于下载书籍，图片，影视数据及缓存相关文件；\n" +
                "3.通讯录，GPS，摄像，麦克风，相册等敏感权限均不会默认开启，只有经过明示授权后才会为实现功能或服务时使用；\n" +
                "4.使用漫画狗APP，即表示你同意该软件的用户协议和隐私政策；");
        //设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 34, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_AGREEMENT)
                        .withInt("setListener", 0)
                        .navigation();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Color.BLUE);
            }
        }, 34, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 34, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new BackgroundColorSpan(Color.WHITE), 34, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_AGREEMENT)
                        .withInt("setListener", 1)
                        .navigation();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Color.BLUE);
            }
        }, 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new BackgroundColorSpan(Color.WHITE), 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    @Override
    public void onAppInitSuccess() {
        readyToGoMain();
    }

    @Override
    public void onAppInitFail() {
        Toaster.showToast(this,"应用初始化失败");
        finish();
    }
}
