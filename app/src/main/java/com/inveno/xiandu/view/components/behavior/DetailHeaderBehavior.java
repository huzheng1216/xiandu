package com.inveno.xiandu.view.components.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Created By huzheng
 * Date 2020/3/31
 * Des
 */
public class DetailHeaderBehavior extends CoordinatorLayout.Behavior<View> {

    //这个构造必须要重载，因为在CoordinatorLayout里利用反射去获取这个Behavior的时候就是拿的这个构造。
    public DetailHeaderBehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

//    @Override
//    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
//        return dependency instanceof AppBarLayout;
//    }
//
//    @Override
//    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
//        float scaleY = Math.abs(dependency.getY()) / dependency.getHeight();
//        child.setTranslationY(child.getHeight() * scaleY);
//        return true;
//    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }
}
