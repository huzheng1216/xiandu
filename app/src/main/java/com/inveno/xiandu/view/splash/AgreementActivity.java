package com.inveno.xiandu.view.splash;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.HeaderBar;

/**
 * Created By huzheng
 * Date 2020-02-17
 * Des 用户协议，隐私政策
 */
@Route(path = ARouterPath.ACTIVITY_AGREEMENT)
public class AgreementActivity extends BaseActivity {

    @Autowired(name = "setListener")
    protected int action = 0;//0: 用户协议，1：隐私政策

    private HeaderBar headerBar;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_agreement);

        headerBar = findViewById(R.id.header);
        content = findViewById(R.id.tv_agreement_content);

        if (action == 0) {
            headerBar.setTitle("用户协议")
                    .showBackImg()
                    .setListener(new HeaderBar.OnActionListener() {
                        @Override
                        public void onAction(int action, Object object) {
                            if (action == HeaderBar.BACK) {
                                finish();
                            }
                        }
                    });
            content.setText(Html.fromHtml(getResources().getString(R.string.user_agreement)));
        } else {
            headerBar.setTitle("隐私政策")
                    .showBackImg()
                    .setListener(new HeaderBar.OnActionListener() {
                        @Override
                        public void onAction(int action, Object object) {
                            if (action == HeaderBar.BACK) {
                                finish();
                            }
                        }
                    });
            content.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy)));
        }
    }
}
