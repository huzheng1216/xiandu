package com.inveno.xiandu.view.components.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.inveno.xiandu.utils.LogUtils;

/**
 * Created By huzheng
 * Date 2020/3/26
 * Des
 */
public class TouchView extends RelativeLayout {

    private TouchListener touchListener;

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    long downTime = 0;
    float downX = 0f, downY = 0f, upX = 0f, upY = 0f;
    boolean isClickDouble;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                LogUtils.H("ACTION_DOWN " + downX + " - " + downY);
                downTime = System.currentTimeMillis();
                //双击事件
                isClickDouble = isFastDoubleClick();
//                if (isFastDoubleClick()) {
//                    touchListener.clickDouble();
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.H("ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                LogUtils.H("ACTION_UP " + upX + " - " + upY + " - " + (System.currentTimeMillis() - downTime));
//                if (Math.abs(upX - downX) < 50 && Math.abs(upY - downY) < 50 && System.currentTimeMillis() - downTime < 200 && !isClickDouble) {
//                    touchListener.clickCenter();
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.H("ACTION_CANCEL");
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 判断是否是快速点击
     */
    private long lastClickTime;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public interface TouchListener {
        void clickCenter();

        void clickDouble();
    }
}
