package com.inveno.xiandu.view.custom;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.inveno.xiandu.R;

/**
 * Created by linlijun on 2019/4/2.
 * desc:适用于该项目的通用底部弹出的pop，有需要拓展的设置，在此类拓展
 */
public class MyPopWindow implements PopupWindow.OnDismissListener, ValueAnimator
        .AnimatorUpdateListener
{
    
    private final int ALPHA_ANIMATOR_DURATION = 200;
    
    private static boolean mOutsideTouchable = true;
    private static boolean mFocusable = true;
    private static boolean mIsHideStateBar = false;
    private static View mLayoutView;
    private static int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private static int mHeight= ViewGroup.LayoutParams.MATCH_PARENT;
    private static int mAnimStyle= R.style.pop_anim_style;
    private PopupWindow mPopWindow;
    private ValueAnimator mBgAlphaAnimator;
    private static Context mContext;
    private static OnPopDismissListener mPopDismissListener;
    private static boolean mIsBgTranslucent = true;
    private static int mOrientation= Gravity.BOTTOM;
    
    
    
    
    public MyPopWindow()
    {
        mPopWindow = new PopupWindow(mLayoutView, mWidth, mHeight);
        mPopWindow.setAnimationStyle(mAnimStyle);
        mPopWindow.setFocusable(mFocusable);
        mPopWindow.setOutsideTouchable(mOutsideTouchable);
        mPopWindow.setOnDismissListener(this);
    }
    
    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        if (mContext != null)
        {
            ((Activity) mContext).getWindow().getAttributes().alpha = (float) animation
                    .getAnimatedValue();
            ((Activity) mContext).getWindow().setAttributes(((Activity) mContext).getWindow()
                    .getAttributes());
        }
    }
    
    @Override
    public void onDismiss()
    {
        if (mIsBgTranslucent)
        {
            mBgAlphaAnimator = ValueAnimator.ofFloat(0.5f, 1.0f);
            mBgAlphaAnimator.start();
            mBgAlphaAnimator.setDuration(ALPHA_ANIMATOR_DURATION);
            mBgAlphaAnimator.addUpdateListener(this);
        }
        
        if (mPopDismissListener != null)
            mPopDismissListener.onDismiss();
    }
    
    public void show()
    {
            if (mPopWindow != null && mLayoutView != null && !mPopWindow.isShowing())
            {
                setAnimAndStateBar();
                mPopWindow.showAtLocation(mLayoutView, mOrientation, 0, 0);
            }
    }

    public void show(int xoff,int yoff)
    {
        if (mPopWindow != null && mLayoutView != null && !mPopWindow.isShowing())
        {
            setAnimAndStateBar();
            mPopWindow.showAtLocation(mLayoutView, mOrientation, xoff, yoff);
        }
    }

    private void setAnimAndStateBar() {
        if (mIsBgTranslucent)
        {
            mBgAlphaAnimator = ValueAnimator.ofFloat(1.0f, 0.5f);
            mBgAlphaAnimator.setDuration(ALPHA_ANIMATOR_DURATION);
            mBgAlphaAnimator.start();
            mBgAlphaAnimator.addUpdateListener(this);
        }
        if (mIsHideStateBar){
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


   /* public void showAsDropDown(View parent,int xoff,int yoff) {
        if (mPopWindow != null && mLayoutView != null && !mPopWindow.isShowing())
        {
            if (mIsBgTranslucent)
            {
                mBgAlphaAnimator = ValueAnimator.ofFloat(1.0f, 0.5f);
                mBgAlphaAnimator.setDuration(ALPHA_ANIMATOR_DURATION);
                mBgAlphaAnimator.start();
                mBgAlphaAnimator.addUpdateListener(this);
            }
            if (mIsHideStateBar){
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
           // mPopWindow.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
           mPopWindow.showAsDropDown(parent,xoff,yoff);
        }
    }*/
    
    public void dismiss()
    {
        if (mPopWindow != null)
            mPopWindow.dismiss();
        
    }
    
    public void destroy()
    {
        if (mPopWindow.isShowing())
            mPopWindow.dismiss();
        mPopWindow = null;
        mContext = null;
        mBgAlphaAnimator = null;
    }
    
    public boolean isShowing()
    {
        return mPopWindow != null && mPopWindow.isShowing();
    }
    
    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnPopDismissListener
    {
        /**
         * Called when this popup window is dismissed.
         */
        public void onDismiss();
    }
    
    public static class Builder
    {
        
        public Builder(Context context)
        {
            if (context == null || !(context instanceof Activity) || ((Activity) context)
                    .isFinishing())
            {
                throw new RuntimeException("activity is invalidate which can not create a " +
                        "popupwindow:"+context);
            }
            mContext = context;
        }
    
        /**
         * 弹窗出现的方向
         * @param orientation  Gravity.BOTTOM  Gravity.RIGHT
         * @return
         */
        public Builder setOrientation(int orientation)
        {
            mOrientation = orientation;
            return this;
        }
        
        public Builder setFocusable(boolean focusable)
        {
            mFocusable = focusable;
            return this;
        }
    
        /**
         * 是否隐藏状态栏
         * @param hideStateBar
         * @return
         */
        public Builder setHideStateBar(boolean hideStateBar)
        {
            mIsHideStateBar = hideStateBar;
            return this;
        }
        
        public Builder setOutsideTouchable(boolean outsideTouchable)
        {
            mOutsideTouchable = outsideTouchable;
            return this;
        }
        
        public Builder setLayoutView(View layout)
        {
            mLayoutView = layout;
            return this;
        }
        
        public Builder setWidth(int width)
        {
            mWidth = width;
            return this;
        }
        
        public Builder setHeight(int height)
        {
            mHeight = height;
            return this;
        }
    
        /**
         * 设置出现消失动画，默认自下而上
         * @param animStyle
         * @return
         */
        public Builder setAnimStyle(int animStyle)
        {
            mAnimStyle = animStyle;
            return this;
        }
        public Builder setBgTranslucent(boolean isBgTranslucent)
        {
            mIsBgTranslucent = isBgTranslucent;
            return this;
        }
        
        public Builder setPopDismissListener(OnPopDismissListener listener)
        {
            mPopDismissListener = listener;
            return this;
        }
        
        public MyPopWindow build()
        {
            
            return new MyPopWindow();
        }
        
    }
    
}
