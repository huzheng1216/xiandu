package com.inveno.xiandu.view.main.welfare;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;

/**
 * @author yongji.wang
 * @date 2020/6/12 13:33
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_COIN_TOP_UP)
public class CoinTopUpActivity extends TitleBarBaseActivity {

    private TextView coin_top_up_sum;
    private TextView coin_top_up_rnb;

    private TextView coin_top_up_coin_10;
    private TextView coin_top_up_coin_30;
    private TextView coin_top_up_coin_50;
    private TextView coin_top_up_coin_100;

    private TextView coin_top_up_rmb_10;
    private TextView coin_top_up_rmb_30;
    private TextView coin_top_up_rmb_50;
    private TextView coin_top_up_rmb_100;

    private LinearLayout coin_top_up_10;
    private LinearLayout coin_top_up_30;
    private LinearLayout coin_top_up_50;
    private LinearLayout coin_top_up_100;

    private EditText coin_top_up_phone_num;

    @Override
    public String getCenterText() {
        return "我要提现";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_coin_top_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    protected void initView() {
        super.initView();

        coin_top_up_sum = findViewById(R.id.coin_top_up_sum);
        coin_top_up_rnb = findViewById(R.id.coin_top_up_rnb);

        coin_top_up_coin_10 = findViewById(R.id.coin_top_up_coin_10);
        coin_top_up_coin_30 = findViewById(R.id.coin_top_up_coin_30);
        coin_top_up_coin_50 = findViewById(R.id.coin_top_up_coin_50);
        coin_top_up_coin_100 = findViewById(R.id.coin_top_up_coin_100);

        coin_top_up_rmb_10 = findViewById(R.id.coin_top_up_rmb_10);
        coin_top_up_rmb_30 = findViewById(R.id.coin_top_up_rmb_30);
        coin_top_up_rmb_50 = findViewById(R.id.coin_top_up_rmb_50);
        coin_top_up_rmb_100 = findViewById(R.id.coin_top_up_rmb_100);

        coin_top_up_10 = findViewById(R.id.coin_top_up_10);
        coin_top_up_30 = findViewById(R.id.coin_top_up_30);
        coin_top_up_50 = findViewById(R.id.coin_top_up_50);
        coin_top_up_100 = findViewById(R.id.coin_top_up_100);

        coin_top_up_phone_num = findViewById(R.id.coin_top_up_phone_num);

        if (ServiceContext.userService().getUserInfo() != null && ServiceContext.userService().getUserInfo().getPhone_num() != null){
            coin_top_up_phone_num.setText(ServiceContext.userService().getUserInfo().getPhone_num());
        }else{
            coin_top_up_phone_num.setHint("请输入电话号码");
        }
    }

    private void choiseMoney(int i) {
        //初始化背景色
        coin_top_up_10.setBackground(getResources().getDrawable(R.drawable.gray_corners_bg));
        coin_top_up_30.setBackground(getResources().getDrawable(R.drawable.gray_corners_bg));
        coin_top_up_50.setBackground(getResources().getDrawable(R.drawable.gray_corners_bg));
        coin_top_up_100.setBackground(getResources().getDrawable(R.drawable.gray_corners_bg));
        //初始化字体颜色
        coin_top_up_coin_10.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_coin_30.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_coin_50.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_coin_100.setTextColor(getResources().getColor(R.color.gray_9));

        coin_top_up_rmb_10.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_rmb_30.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_rmb_50.setTextColor(getResources().getColor(R.color.gray_9));
        coin_top_up_rmb_100.setTextColor(getResources().getColor(R.color.gray_9));

        switch (i) {
            case 0:
                setChoise(coin_top_up_10, coin_top_up_coin_10, coin_top_up_rmb_10);
                break;
            case 1:
                setChoise(coin_top_up_30, coin_top_up_coin_30, coin_top_up_rmb_30);
                break;
            case 2:
                setChoise(coin_top_up_50, coin_top_up_coin_50, coin_top_up_rmb_50);
                break;
            case 3:
                setChoise(coin_top_up_100, coin_top_up_coin_100, coin_top_up_rmb_100);
                break;

        }
    }

    private void setChoise(ViewGroup mViewGroup, TextView tv1, TextView tv2) {
        mViewGroup.setBackground(getResources().getDrawable(R.drawable.blue_corners_bg_8));
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv2.setTextColor(getResources().getColor(R.color.white));
    }

    public void choise_10(View view) {
        choiseMoney(0);
    }

    public void choise_30(View view) {
        choiseMoney(1);
    }

    public void choise_50(View view) {
        choiseMoney(2);
    }

    public void choise_100(View view) {
        choiseMoney(3);
    }

    public void exchange(View view) {
        Toaster.showToastCenter(this, "功能暂未开放，敬请期待");
    }

    public void coinRecord(View view) {
        Toaster.showToastCenter(this, "功能暂未开放，敬请期待");
    }

}
