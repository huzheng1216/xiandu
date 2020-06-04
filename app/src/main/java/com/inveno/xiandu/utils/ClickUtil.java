package com.inveno.xiandu.utils;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * creat by: huzheng
 * date: 2019-12-06
 * description:点击事件管理
 */
public class ClickUtil {

    /**
     * @author huzheng
     * @date 2019-12-06
     * @description
     * 一个时间段内，只取第一次点击
     */
    public static void bindSingleClick(final View v, int duration, final View.OnClickListener onClickListener) {

        int d = duration > 0 ? duration : 200;
        RxView.clicks(v)
                .throttleFirst(d, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onClickListener.onClick(v);
                    }
                });
    }

}
