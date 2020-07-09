package com.inveno.xiandu.view.main.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/15 10:28
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_READ_GENDER)
public class ChoiseGenderActivity extends BaseActivity {
    public static final int MAIN_REQUEST_CODE = 10001;

    public static final int GENDER_MAN = 1;
    public static final int GENDER_WOMAN = 2;
    public static final int GENDER_OTHER = 0;

    private TextView choise_message;
    private TextView gender_other;

    private int requestCode = MAIN_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_choise_gender);
        initView();
    }

    private void initView() {
        choise_message = findViewById(R.id.choise_message);
        gender_other = findViewById(R.id.gender_other);

        requestCode = getIntent().getIntExtra("request_code", 10001);
        if (requestCode == MAIN_REQUEST_CODE) {
            gender_other.setVisibility(View.VISIBLE);
        } else {
            gender_other.setVisibility(View.GONE);
        }
    }

    public void click_choise_man(View view) {
        choiseFinish(GENDER_MAN);
    }

    public void click_choise_woman(View view) {
        choiseFinish(GENDER_WOMAN);

    }

    public void click_choise_other(View view) {
        choiseFinish(GENDER_OTHER);

    }

    private void choiseFinish(int gender) {
        choise_message.setText("正在为您准备内容");
        APIContext.updataUserAPI().addPreference(gender)
                .onSuccess(new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        SPUtils.setInformain(Keys.READ_LIKE, gender);
                        if (requestCode == MAIN_REQUEST_CODE) {
                            //第一次启动完成
                            SPUtils.setInformain(Keys.FIRST_LAUNCH_KEY, true);
                            ARouter.getInstance().build(ARouterPath.ACTIVITY_MAIN)
                                    .navigation();
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("gender", gender);
                            setResult(requestCode, intent);
                            finish();
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        Toaster.showToastCenter(ChoiseGenderActivity.this, "上传偏好失败");
                        return null;
                    }
                }).execute();
    }
}
