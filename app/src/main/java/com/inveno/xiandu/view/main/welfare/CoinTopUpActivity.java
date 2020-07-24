package com.inveno.xiandu.view.main.welfare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.StringTools;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

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

    private RadioButton operator_yidong;//移动
    private RadioButton operator_liantong;//联通
    private RadioButton operator_dianxin;//电信

    private UserCoin mUserCoin;

    private int rechargeId = 1;
    private int operator = 1;
    private boolean coinFull = false;
    int rate = 10000;

    @Override
    public String getCenterText() {
        return "话费充值";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_coin_top_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        String mUserCoinStr = getIntent().getStringExtra("mUserCoin");
        mUserCoin = GsonUtil.gsonToObject(mUserCoinStr, UserCoin.class);
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

        operator_yidong = findViewById(R.id.operator_yidong);
        operator_liantong = findViewById(R.id.operator_liantong);
        operator_dianxin = findViewById(R.id.operator_dianxin);

        coin_top_up_phone_num = findViewById(R.id.coin_top_up_phone_num);
        setCursorColor(coin_top_up_phone_num);

        if (ServiceContext.userService().getUserInfo() != null && ServiceContext.userService().getUserInfo().getPhone_num() != null) {
            coin_top_up_phone_num.setText(ServiceContext.userService().getUserInfo().getPhone_num());
        } else {
            coin_top_up_phone_num.setHint("请输入电话号码");
        }

        if (mUserCoin != null) {
            setView();
        } else {
            APIContext.coinApi().queryCoin()
                    .onSuccess(new Function1<UserCoinOut, Unit>() {
                        @Override
                        public Unit invoke(UserCoinOut userCoin) {
                            mUserCoin = userCoin.getCoin();
                            setView();
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            return null;
                        }
                    }).execute();
        }

    }

    private void setView() {
        coin_top_up_sum.setText(String.valueOf(mUserCoin.getBalance()));
        try {
            DecimalFormat df = new DecimalFormat("######0.00");
            rate = Integer.parseInt(mUserCoin.getExchage_rate().split("金币")[0]);
            String rnbStr = df.format(mUserCoin.getBalance() / (double) rate);
            coin_top_up_rnb.setText(String.format("(约%s元)", rnbStr));
        } catch (Exception e) {

        }
    }

    private void choiseMoney(int i) {
        rechargeId = i + 1;
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
                coinFull = mUserCoin.getBalance() >= rate * 10;
                setChoise(coin_top_up_10, coin_top_up_coin_10, coin_top_up_rmb_10);
                break;
            case 1:
                coinFull = mUserCoin.getBalance() >= rate * 30;
                setChoise(coin_top_up_30, coin_top_up_coin_30, coin_top_up_rmb_30);
                break;
            case 2:
                coinFull = mUserCoin.getBalance() >= rate * 50;
                setChoise(coin_top_up_50, coin_top_up_coin_50, coin_top_up_rmb_50);
                break;
            case 3:
                coinFull = mUserCoin.getBalance() >= rate * 100;
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
        if (coinFull) {
            String telephone = coin_top_up_phone_num.getText().toString();
            if (!TextUtils.isEmpty(telephone)) {
                if (StringTools.isPhone(telephone)) {
                    Toaster.showToastCenter(this, "金币余额不足");
                    //获取运营商
                    if (operator_yidong.isChecked()) {
                        //移动
                        operator = 1;
                    } else if (operator_liantong.isChecked()) {
                        //联通
                        operator = 2;
                    } else if (operator_dianxin.isChecked()) {
                        //电信
                        operator = 4;
                    } else {
                        //未知
                        operator = 1;
                    }
                    Objects.requireNonNull(APIContext.getWelfareApi().topUpTelephone(rechargeId, telephone, operator))
                            .onSuccess(new Function1<String, Unit>() {
                                @Override
                                public Unit invoke(String s) {
                                    Toaster.showToastCenterShort(CoinTopUpActivity.this, "充值订单已提交，我们会在3个工作日内审核并为您充值");
                                    return null;
                                }
                            })
                            .onFail(new Function2<Integer, String, Unit>() {
                                @Override
                                public Unit invoke(Integer integer, String s) {
                                    return null;
                                }
                            }).execute();
                } else {
                    Toaster.showToastCenter(this, "手机号有误");
                }
            } else {
                Toaster.showToastCenter(this, "请输入电话号码");
            }
        } else {
            Toaster.showToastCenter(this, "您的金币余额不足无法充值");
        }
    }

    public void coinRecord(View view) {
        startActivity(new Intent(this, TopUpRecordActivity.class));
    }


    /**
     * 反射设置光标颜色
     *
     * @param mEditText
     */
    private void setCursorColor(EditText mEditText) {

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mEditText, R.drawable.my_cursor);
        } catch (Exception ignored) {
        }

    }

}
