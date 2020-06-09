package com.inveno.xiandu.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.inveno.xiandu.R;

/**
 * @author yongji.wang
 * @date 2020/6/8 10:56
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;
    private Context mContext;

    /**
     * @param textView          显示倒计时的textview
     * @param millisInFuture    倒计时时间
     * @param countDownInterval 倒计时间隔
     */
    public CountDownTimerUtils(Context context, TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
        mContext = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "s");  //设置倒计时时间
        mTextView.setTextColor(Color.parseColor("#999999")); //设置按钮为灰色，这时是不能点击的
    }

    @Override
    public void onFinish() {
        mTextView.setText(mContext.getResources().getString(R.string.login_resend_vali_code));
        mTextView.setClickable(true);//重新获得点击
        mTextView.setTextColor(Color.parseColor("#4B7CFF"));  //还原背景色
    }
}
