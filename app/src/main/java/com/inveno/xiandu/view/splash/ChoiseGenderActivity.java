package com.inveno.xiandu.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;

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
    public static final int MINE_REQUEST_CODE = 10002;

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

    private void initView(){
        choise_message = findViewById(R.id.choise_message);
        gender_other = findViewById(R.id.gender_other);

        requestCode = getIntent().getIntExtra("request_code", 10001);
        if (requestCode == MAIN_REQUEST_CODE){
            gender_other.setVisibility(View.VISIBLE);
        }else{
            gender_other.setVisibility(View.GONE);
        }
    }

    public void click_choise_man(View view){
        Toaster.showToast(this,"选择男生频道");
        choiseFinish(GENDER_MAN);
    }

    public void click_choise_woman(View view){
        Toaster.showToast(this,"选择女生频道");
        choiseFinish(GENDER_WOMAN);

    }

    public void click_choise_other(View view){
        Toaster.showToast(this,"选择随便看看");
        choiseFinish(GENDER_OTHER);

    }

    private void choiseFinish(int gender){
        choise_message.setText("正在为您准备内容");
        if (requestCode == MAIN_REQUEST_CODE){
            // TODO: 2020/6/15 这里可能需要请求数据
        }else{

        }
        Intent intent = new Intent();
        intent.putExtra("gender", gender);
        setResult(requestCode, intent);
        finish();
    }
}
