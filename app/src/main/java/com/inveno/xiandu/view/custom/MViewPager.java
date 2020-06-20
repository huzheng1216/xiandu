package com.inveno.xiandu.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @author yongji.wang
 * @date 2020/6/18 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class MViewPager extends ViewPager {


    // 是否禁止 viewpager 左右滑动
    private boolean noScroll = true;

    public MViewPager(@NonNull Context context) {
        super(context);
    }

    public MViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void setScrollable(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (noScroll) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (noScroll) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

}
