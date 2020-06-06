package com.inveno.xiandu.view.book;

import android.app.Activity;
import android.os.Build;

public class A {
    public static boolean fullscreen = true;
    public static boolean fitHardwareAccelerated;
    public static boolean openBookAnim;



    public static void disableTransOverlap(Activity act) {
        if (A.openBookAnim && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.getWindow().setAllowEnterTransitionOverlap(false);
            act.getWindow().setAllowReturnTransitionOverlap(false);
            act.getWindow().setEnterTransition(null);
            act.getWindow().setExitTransition(null);
            act.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
