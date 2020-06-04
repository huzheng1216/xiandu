package com.inveno.xiandu.view.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.AnimationTools;


/**
 * Created By huzheng
 * Date 2020/4/1
 * Des 配合CoordinatorLayout特效的下拉/上拉 拖拽阻尼效果
 * 增加边界回弹效果
 */
public class PullRecyclerViewGroup2 extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * 滚动时间
     */
    private static final long ANIM_TIME = 150;

    //直接子view
    private View childView;

    // 用于记录正常的布局位置
    private Rect originalRect = new Rect();
    private Rect recyclerviewRect = new Rect();

    // 在手指滑动的过程中记录是否移动了布局
    private boolean isMoved = false;

    // 如果按下时不能上拉和下拉， 会在手指移动时更新为当前手指的Y值
    private float startY;

    //隐藏栏是否完全可见
    public boolean isTopVisible;

    //阻尼
    private static final float OFFSET_RADIO = 0.3f;

    private boolean isRecyclerReuslt = false;

    private RecyclerView recyclerView;
    private int oldState;
    private int speed;


    public PullRecyclerViewGroup2(Context context) {
        this(context, null);
    }

    public PullRecyclerViewGroup2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecyclerViewGroup2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //关闭右侧滚动条
        this.setVerticalScrollBarEnabled(false);
    }

    /**
     * 加载布局后初始化,这个方法会在加载完布局后调用
     */
    @Override
    protected void onFinishInflate() {
        childView = getChildAt(0);
        recyclerView = findViewById(R.id.recyclerView);
        if (childView == null) {
            throw new RuntimeException("PullRecyclerViewGroup 子容器中必须有一个RecyclerView、ListView或者ScrollView");
        }
        //布局重绘监听，比如华为屏幕键盘可以弹出和隐藏，改变布局，加监听就可以虽键盘弹出关闭的变化而变化
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        //监听recyclerview，自动滚动停止时，是否需要进行动画
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0 && oldState == 2) {
                    //自动滚动结束，判断是否到边界
                    if (isCanPullDown() || isCanPullUp()) {
                        //执行边界动画
                        doTanTanLe();
                    }
                }
                oldState = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                speed = dy;
            }
        });

        super.onFinishInflate();
    }

    /**
     * recyclerview边界动画
     */
    private void doTanTanLe() {
//        Toaster.showToastCenter(getContext(), speed > 0 ? ("下边界动画 " + speed) : ("上边界动画" + speed));
        AnimationTools.startScaleYAnimal(childView, speed);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //ScrollView中唯一的子控件的位置信息，这个位置在整个控件的生命周期中保持不变
        originalRect.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
        recyclerviewRect.set(recyclerView.getLeft(), recyclerView.getTop(), recyclerView.getRight(), recyclerView.getBottom());
    }

    /**
     * 事件分发
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (childView == null) {
            return super.dispatchTouchEvent(ev);
        }

        boolean isTouchOutOfScrollView = ev.getY() >= originalRect.bottom || ev.getY() <= originalRect.top; //如果当前view的Y上的位置
        if (isTouchOutOfScrollView) {//如果不在view的范围内
            if (isMoved) {      //当前容器已经被移动
                recoverLayout();
            }
            return true;
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //记录按下时的Y
                startY = ev.getY();
            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                int scrollY = (int) (nowY - startY);
//                L.H(scrollY + " - " + isCanPullDown() + " - " + isCanPullUp());
                if ((isCanPullDown() && scrollY > 0 && isTopVisible) || (isCanPullUp() && scrollY < 0) || (isCanPullDown() && isCanPullUp())) {
                    int offset = (int) (scrollY * OFFSET_RADIO);

                    if (isCanPullUp() || isCanPullDown()) {
                        childView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
                    } else {
                        recyclerView.layout(recyclerviewRect.left, recyclerviewRect.top + offset, recyclerviewRect.right, recyclerviewRect.bottom + offset);
                    }
                    isMoved = true;
                    isRecyclerReuslt = false;
                    return true;
                } else {
                    startY = ev.getY();
                    isMoved = false;
                    isRecyclerReuslt = true;
                    recoverLayout();
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:

                if (isMoved) {
                    recoverLayout();
                }

                if (isRecyclerReuslt) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return true;
                }
            default:
                return true;
        }

    }

    /**
     * 位置还原
     */
    private void recoverLayout() {

        if (!isMoved) {
            return;//如果没有移动布局，则跳过执行
        }

        if (isCanPullUp() || isCanPullDown()) {
            TranslateAnimation anim = new TranslateAnimation(0, 0, childView.getTop() - originalRect.top, 0);
            anim.setDuration(ANIM_TIME);
            childView.startAnimation(anim);
            childView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
            recyclerView.requestLayout();
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, recyclerView.getTop() - recyclerviewRect.top, 0);
            anim.setDuration(ANIM_TIME);
            recyclerView.startAnimation(anim);
            recyclerView.layout(recyclerviewRect.left, recyclerviewRect.top, recyclerviewRect.right, recyclerviewRect.bottom);
        }

        isMoved = false;
    }

    /**
     * 容器的的事件都在事件分发中处理，这里处理的是事件分发传递过来的事件，
     * <p>
     * 传递过来的为RecyclerVIew的事件  不拦截，直接交给reyclerview处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;  //不拦截  直接传递给子的view
    }


    /**
     * 判断是否可以下拉
     * <p>
     * computeVerticalScrollOffset：计算控件垂直方向的偏移值，
     * <p>
     * computeVerticalScrollExtent：计算控件可视的区域，
     * <p>
     * computeVerticalScrollRange：计算控件垂直方向的滚动范围
     *
     * @return
     */
    private boolean isCanPullDown() {
        if (recyclerView == null) return false;
        return !recyclerView.canScrollVertically(-1);
    }


    /**
     * 判断是否可以上拉
     *
     * @return
     */
    private boolean isCanPullUp() {
        if (recyclerView == null) return false;
        return !recyclerView.canScrollVertically(1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGlobalLayout() {
        //华为手机屏幕下方的返回、home键显示隐藏改变布局
        requestLayout();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

}
