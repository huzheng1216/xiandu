package com.inveno.xiandu.view.user.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.utils.CountDownTimerUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/8 10:00
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ValiCodeActivity extends TitleBarBaseActivity implements View.OnClickListener {

    public static String LOGIN_PHONE_NUM = "login_phone_num";
    private TextView vali_time;
    private TextView vali_login;
    private TextView vali_phone_num;
    private EditText vali_code_input_editview;
    private CountDownTimerUtils countDownTimerUtils;
    private String loginPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    public String getCenterText() {
        return getResources().getString(R.string.login);
    }

    @Override
    protected void initView() {
        super.initView();
        vali_time = findViewById(R.id.vali_time);
        vali_login = findViewById(R.id.vali_login);
        vali_code_input_editview = findViewById(R.id.vali_code_input_editview);
        vali_phone_num = findViewById(R.id.vali_phone_num);

        loginPhoneNum = getIntent().getStringExtra(LOGIN_PHONE_NUM);
        vali_phone_num.setText(String.format(getResources().getString(R.string.login_send_vali_cade_phone_num), loginPhoneNum));

        vali_time.setOnClickListener(this);
        vali_login.setOnClickListener(this);

        editListener();
        countDownTimerUtils = new CountDownTimerUtils(this, vali_time, 60 * 1000, 1000);
        countDownTimerUtils.start();
    }

    @Override
    public int layoutID() {
        return R.layout.activity_valication_code;
    }

    /**
     * 输入监听
     */
    private void editListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    vali_login.setBackground(getResources().getDrawable(R.drawable.button_radio_background_disclick));
                    vali_login.setEnabled(false);
                    vali_login.setClickable(false);
                } else {
                    vali_login.setBackground(getResources().getDrawable(R.drawable.button_radio_background));
                    vali_login.setEnabled(true);
                    vali_login.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        vali_code_input_editview.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.vali_time) {
            // TODO: 2020/6/8 重新发送验证码请求
            APIContext.varicationCode().getVaricationCode(loginPhoneNum, "3")
                    .onSuccess(new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            Toaster.showToast(ValiCodeActivity.this, "验证码:" + s);
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            Toaster.showToast(ValiCodeActivity.this, getResources().getString(R.string.login_get_vali_code_fail));
                            return null;
                        }
                    })
                    .execute();
            countDownTimerUtils.start();

        } else if (id == R.id.vali_login) {
            // TODO: 2020/6/8 发送登录请求
            Toaster.showToast(ValiCodeActivity.this, "登录请求");
            APIContext.loginAPI().login(loginPhoneNum, vali_code_input_editview.getText().toString())
                    .onSuccess(new Function1<UserInfo, Unit>() {
                        @Override
                        public Unit invoke(UserInfo userInfos) {
                            Toaster.showToast(ValiCodeActivity.this, getResources().getString(R.string.login_success));
                            setResult(LoginOtherPhoneActivity.LOGIN_VALI_CODE_REBACK);
                            finish();
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            Toaster.showToast(ValiCodeActivity.this, getResources().getString(R.string.login_fail));
                            return null;
                        }
                    }).execute();
        }
    }
}
