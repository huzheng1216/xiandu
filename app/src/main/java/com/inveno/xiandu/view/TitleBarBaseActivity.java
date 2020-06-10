package com.inveno.xiandu.view;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.provider.titlebar.ITittleBaseActivity;
import com.inveno.xiandu.view.BaseActivity;

/**
 * @author yongji.wang
 * @date 2020/6/6 13:08
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public abstract class TitleBarBaseActivity extends BaseActivity implements ITittleBaseActivity {
    /**
     * 标题中部文字描述
     */
    private TextView titleText;
    /**
     * 标题右边View布局
     */
    private LinearLayout rightLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCreateView();
        initView();
    }

    /**
     * 设置布局文件
     */
    protected void initCreateView() {
        if (layoutID() != 0){
            setContentView(layoutID());
        }
    }

    protected void initView() {

        //添加右边按钮
        rightLayout = findViewById(R.id.base_activity_right);
        if (getRightBtn() != null) {
            rightLayout.addView(getRightBtn());
        }

        titleText = findViewById(R.id.base_activity_title);
        if (getCenterText() != null) {
            titleText.setText(getCenterText());
        }

        try {
            View titlebarView = findViewById(R.id.title_bar_back);
            if (titlebarView != null) {
                titlebarView.setOnClickListener(new MyBackListener());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyBackListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (getLeftBtn()) {
            } else {
                finish();
            }
        }
    }

    /**
     * 获取右边按钮
     *
     * @return
     */
    protected boolean getLeftBtn() {
        return false;
    }

    ;

    /**
     * 获取右边按钮
     *
     * @return
     */
    protected View getRightBtn() {
        return null;
    }

    public abstract String getCenterText();

}
