package com.inveno.xiandu.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class BookDetailScrollView extends ScrollView {

    private OnScrollBottomListener onScrollBottomListener;

    public BookDetailScrollView(Context context) {
        super(context);
    }

    public BookDetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookDetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (getChildAt(0).getHeight() == getHeight() + getScrollY()){
            if (onScrollBottomListener!=null){
                onScrollBottomListener.scrollBottom();
            }
        }
    }

    public interface OnScrollBottomListener{
        void scrollBottom();
    }

    public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
        this.onScrollBottomListener = onScrollBottomListener;
    }
}
