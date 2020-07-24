package com.inveno.xiandu.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author yongji.wang
 * @date 2020/7/15
 * @更新说明：
 * @更新时间：2020/7/15
 * @Version：1.0.0
 */
public class MWebview extends WebView {
    public MWebview(Context context) {
        super(getFixedContext(context));
    }

    public MWebview(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    public MWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
    }

    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        return context;
    }
}
