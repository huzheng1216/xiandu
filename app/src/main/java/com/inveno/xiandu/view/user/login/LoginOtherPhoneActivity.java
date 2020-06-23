package com.inveno.xiandu.view.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.StringTools;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;

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
            Toaster.showToast(this, getResources().getString(R.string.login_get_code));
            // TODO: 2020/6/6 发送给验证码请求，成功后跳转
            APIContext.varicationCode().getVaricationCode(login_phone_edit.getText().toString(), "3")
                    .onSuccess(new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            Toaster.showToast(LoginOtherPhoneActivity.this, "验证码:" + s);
                            Intent intent = new Intent(LoginOtherPhoneActivity.this, ValiCodeActivity.class);
                            intent.putExtra(ValiCodeActivity.LOGIN_PHONE_NUM, login_phone_edit.getText().toString());
                            startActivityForResult(intent, LOGIN_VALI_CODE_REBACK);
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            Log.i("wyjjjjjj", "onFail: " + s);
                            Toaster.showToast(LoginOtherPhoneActivity.this, getResources().getString(R.string.login_get_vali_code_fail) + ":" + s);
                            return null;
                        }
                    })
                    .execute();
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
