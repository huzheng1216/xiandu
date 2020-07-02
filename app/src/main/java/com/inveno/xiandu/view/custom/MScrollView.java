package com.inveno.xiandu.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * @author yongji.wang
 * @date 2020/6/30 19:45
 * @更新说明：
 * @更新时间：
 * @Version：
 */
public class MScrollView extends ScrollView {

    private OnScrollBottomListener onScrollBottomListener;
    private ScrollViewListener scrollViewListener = null;
    public MScrollView(Context context) {
        super(context);
    }
    public MScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }
    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
        if (getChildAt(0).getHeight() == getHeight() + getScrollY()){
            if (onScrollBottomListener!=null){
                onScrollBottomListener.scrollBottom();
            }
        }
    }


    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface OnScrollBottomListener{
        void scrollBottom();
    }

    public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
        this.onScrollBottomListener = onScrollBottomListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(MScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}
