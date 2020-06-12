package com.inveno.xiandu.view.custom;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.inveno.xiandu.R;
/**
 * 作者：wyj
 * 时间：2020/4/3
 * 版本：${VERSION_NAME}
 * 功能：
 * 修改历史：
 */
public class MyScrollView extends ScrollView
{
    private int mMaxHeight;
    
    public MyScrollView(Context context) {
        super(context);
    }
    
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }
    
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }
    
    private void initialize(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyScrollView);
        mMaxHeight = typedArray.getLayoutDimension(R.styleable.MyScrollView_maxHeight, mMaxHeight);
        typedArray.recycle();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
