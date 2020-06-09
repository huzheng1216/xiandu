package com.inveno.xiandu.view.user.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.Toaster;

/**
 * @author yongji.wang
 * @date 2020/6/6 11:58
 * @更新说明：登录页面
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_LOGIN)
public class LoginActivity extends TitleBarBaseActivity implements View.OnClickListener {

    private TextView login_my_phone, login_one_key, login_other_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    protected void initView() {
        super.initView();
        login_my_phone = findViewById(R.id.login_my_phone);

        login_one_key = findViewById(R.id.login_one_key);
        login_other_phone = findViewById(R.id.login_other_phone);

        login_one_key.setOnClickListener(this);
        login_other_phone.setOnClickListener(this);
    }

    @Override
    public int layoutID() {
        return R.layout.activity_login;
    }

    @Override
    public String getCenterText() {
        return getResources().getString(R.string.login);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.login_one_key){
            Toaster.showToastCenter(this, "一键登录");
        }
        else if (v.getId()==R.id.login_other_phone){
            Toaster.showToastCenter(this, "其他手机");
        }
    }
}
