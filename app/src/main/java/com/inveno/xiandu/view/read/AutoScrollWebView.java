package com.inveno.xiandu.view.read;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created By huzheng
 * Date 2020/3/13
 * Des 可滚动到底部的webview
 */
public class AutoScrollWebView extends WebView {
    public AutoScrollWebView(Context context) {
        super(context);
    }

    public AutoScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    int he = 0;

    //实例化WebViwe后，调用此方法可滚动到底部
    public void scrollToBottom() {
        int h = computeVerticalScrollRange();
        he += h;
        scrollTo(0, he);
    }
}
