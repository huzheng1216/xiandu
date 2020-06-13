package com.inveno.xiandu.view.main.welfare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.TitleBarBaseActivity;

/**
 * @author yongji.wang
 * @date 2020/6/11 16:16
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_MY_COIN)
public class MyCoinActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin);
        setStatusBar(R.color.my_coin_top_color, true);
        initView();
    }

    private void initView() {
        ImageView title_bar_back = findViewById(R.id.title_bar_back);
        title_bar_back.setColorFilter(Color.WHITE);
    }

    public void back(View view){
        finish();
    }

    public void coin_top_up(View view){
        ARouter.getInstance().build(ARouterPath.ACTIVITY_COIN_TOP_UP).navigation();
    }
}
