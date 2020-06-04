package com.inveno.xiandu.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.inveno.xiandu.R;


/**
 * Created By huzheng
 * Date 2020/3/16
 * Des 动画工具
 */
public class AnimationTools {

    /**
     * 果冻弹性的放大效果
     * size 放大倍数（基数为：1,大于1则放大，小于1则缩小）
     */
    public static void startJdllyAnimal(View view, float size) {
        SpringAnimation animationX = new SpringAnimation(view, SpringAnimation.SCALE_X, size);
        SpringAnimation animationY = new SpringAnimation(view, SpringAnimation.SCALE_Y, size);
        animationX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationX.setStartValue(1.0f);

        animationY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationY.setStartValue(1.0f);

        animationX.start();
        animationY.start();
    }


    /**
     * Y轴弹簧效果
     * size 放大倍数（基数为：1,大于1则放大，小于1则缩小）
     */
    public static void startScaleYAnimal(View view, float size) {
//        final float s = speed * 10;
//        final long t = Math.abs(speed*3);
        if (size == 0) {
            return;
        }
        SpringAnimation animationY = new SpringAnimation(view, SpringAnimation.TRANSLATION_Y, 0);
        //刚度(劲度/弹性)，刚度越大，形变产生的里也就越大，体现在效果上就是运动越快
        animationY.getSpring().setStiffness(Math.abs(size * 20));
        //阻尼系数，系数越大，动画停止的越快。从理论上讲分为三种情况 Overdamped过阻尼（ζ > 1）、Critically damped临界阻尼(ζ = 1)、Underdamped欠阻尼状态(0<ζ <1)。
        animationY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        animationY.setStartValue(-size);
        animationY.start();
    }


    public static Animation getTopIn(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.top_in);
    }

    public static Animation getTopOut(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.top_out);
    }

    public static Animation getBottomIn(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.bottom_in);
    }

    public static Animation getBottomOut(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.bottom_out);
    }

}
