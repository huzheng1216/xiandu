package com.inveno.xiandu.view.user.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.StringTools;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/6 11:58
 * @更新说明：登录页面
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE)
public class LoginOtherPhoneActivity extends TitleBarBaseActivity implements View.OnClickListener {

    public static int LOGIN_VALI_CODE_REBACK = 10000;
    private EditText login_phone_edit;
    private TextView login_get_code;
    private TextView agree_tv;
    private CheckBox agree_cb;

    private String lastPhone = "";
    private long lastLoginTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    protected void initView() {
        super.initView();
        login_phone_edit = findViewById(R.id.login_phone_edit);
        login_get_code = findViewById(R.id.login_get_code);

        agree_cb = findViewById(R.id.agree_cb);
        agree_tv = findViewById(R.id.agree_tv);
        agree_tv.setText(getClickableSpan());
        agree_tv.setMovementMethod(LinkMovementMethod.getInstance());
        // 去掉点击后文字的背景色
        agree_tv.setHighlightColor(Color.parseColor("#00000000"));

        login_get_code.setOnClickListener(this);
        editListener();
    }

    private void editListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringTools.isPhone(s.toString())) {
                    login_get_code.setBackground(getResources().getDrawable(R.drawable.button_radio_background_disclick));
                    login_get_code.setEnabled(false);
                    login_get_code.setClickable(false);
                } else {
                    login_get_code.setBackground(getResources().getDrawable(R.drawable.button_radio_background));
                    login_get_code.setEnabled(true);
                    login_get_code.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        login_phone_edit.addTextChangedListener(textWatcher);
    }

    //设置超链接文字
    private SpannableString getClickableSpan() {
        SpannableString spanStr = new SpannableString("我已阅读并同意《用户协议》及《隐私政策》");
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
        }, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        }, 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    @Override
    public int layoutID() {
        return R.layout.activity_login_other_phone;
    }

    @Override
    public String getCenterText() {
        return getResources().getString(R.string.login);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_get_code) {
            if (agree_cb.isChecked()) {
                if (lastPhone.equals(login_phone_edit.getText().toString())) {
                    Intent intent = new Intent(LoginOtherPhoneActivity.this, ValiCodeActivity.class);
                    intent.putExtra(ValiCodeActivity.LOGIN_PHONE_NUM, login_phone_edit.getText().toString());
                    if ((System.currentTimeMillis() - lastLoginTime) / 1000 >= 60) {
                        intent.putExtra(ValiCodeActivity.LOGIN_TIME, 0);
                    } else {
                        intent.putExtra(ValiCodeActivity.LOGIN_TIME, (int) (60 - ((System.currentTimeMillis() - lastLoginTime) / 1000)));
                    }
                    startActivityForResult(intent, LOGIN_VALI_CODE_REBACK);
                } else {
                    Toaster.showToastCenterShort(this, getResources().getString(R.string.login_get_code));
                    // TODO: 2020/6/6 发送给验证码请求，成功后跳转
                    APIContext.varicationCode().getVaricationCode(login_phone_edit.getText().toString(), "3")
                            .onSuccess(new Function1<String, Unit>() {
                                @Override
                                public Unit invoke(String s) {
                                    lastPhone = login_phone_edit.getText().toString();
                                    lastLoginTime = System.currentTimeMillis();
                                    Toaster.showToast(LoginOtherPhoneActivity.this, "验证码:" + s);
                                    Intent intent = new Intent(LoginOtherPhoneActivity.this, ValiCodeActivity.class);
                                    intent.putExtra(ValiCodeActivity.LOGIN_PHONE_NUM, login_phone_edit.getText().toString());
                                    intent.putExtra(ValiCodeActivity.LOGIN_TIME, 60);
                                    startActivityForResult(intent, LOGIN_VALI_CODE_REBACK);
                                    return null;
                                }
                            })
                            .onFail(new Function2<Integer, String, Unit>() {
                                @Override
                                public Unit invoke(Integer integer, String s) {
                                    Log.i("wyjjjjjj", "onFail: " + s);
                                    Toaster.showToastCenterShort(LoginOtherPhoneActivity.this, getResources().getString(R.string.login_get_vali_code_fail) + ":" + s);
                                    return null;
                                }
                            })
                            .execute();
                }
            } else {
                Toaster.showToastCenterShort(LoginOtherPhoneActivity.this, "您需要阅读并同意《用户协议》及《隐私政策》");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_VALI_CODE_REBACK && resultCode == LOGIN_VALI_CODE_REBACK) {

            finish();
        }
    }
}
