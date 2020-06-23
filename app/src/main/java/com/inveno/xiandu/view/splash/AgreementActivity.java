package com.inveno.xiandu.view.splash;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.HeaderBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            content.setText(Html.fromHtml(readAsset("privacy_agreement.txt")));
        } else {
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
            content.setText(Html.fromHtml(readAsset("user_agreement.txt")));
        }
    }

    public String readAsset(String agreementPath) {
        try {
            //获取文件中的字节
            InputStream inputStream = getResources().getAssets().open(agreementPath);
            //将字节转换为字符
            InputStreamReader isReader = new InputStreamReader(inputStream, "UTF-8");
            //使用bufferReader去读取内容
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder result = new StringBuilder();
            String out = "";
            while ((out = reader.readLine()) != null) {
                result.append(out);
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
