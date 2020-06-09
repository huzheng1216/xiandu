package com.inveno.xiandu.view.book;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.bean.book.BookContent;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.DividerItemDecorationStyleOne;
import com.inveno.xiandu.view.components.content.ContentControlView;
import com.inveno.xiandu.view.components.content.ZoomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created By huzheng
 * Date 2020-02-11
 * Des 目录页
 */
@Route(path = ARouterPath.ACTIVITY_CONTENT_MAIN)
public class ActivityReader extends BaseActivity {

    //数据
    @Autowired(name = "json")
    protected String json;
    @Autowired(name = "sectionUrl")
    protected String sectionUrl;
    @Autowired(name = "sectionName")
    protected String sectionName;
    private Book book;
    private List<BookContent> list = new ArrayList<>();
    //控件
    private ZoomRecyclerView recyclerView;
//    private TouchView touchView;
    private ContentControlView contentControlView;
    private ContentAdapter contentAdapter;
    private AutoScrollWebView mWebView;

    //任务
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
//        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_book_content_main);

        mWebView = findViewById(R.id.webView);
        recyclerView = findViewById(R.id.recyclerView);
//        touchView = findViewById(R.id.view_touch);
        contentControlView = findViewById(R.id.content_control);
//        touchView.setTouchListener(new TouchView.TouchListener() {
//            @Override
//            public void clickCenter() {
//                contentControlView.setVisibility(View.VISIBLE);
//                contentControlView.centerClick();
//            }
//
//            @Override
//            public void clickDouble() {
////                recyclerView.setEnableScale();
//            }
//        });
        contentAdapter = new ContentAdapter(this, list);
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecorationStyleOne(this, DividerItemDecorationStyleOne.VERTICAL_LIST, R.drawable.divider_recycleview, 0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setEnableScale(true);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setZoomListener(new ZoomRecyclerView.ZoomListener() {
            @Override
            public void onCenterClick() {
                contentControlView.setVisibility(View.VISIBLE);
                contentControlView.centerClick();
            }
        });
//        contentAdapter.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(BookContent bookContent) {
//            }
//        });
        initData();
    }

    private void initData() {
        book = GsonUtil.gsonToObject(json, Book.class);

        if (book == null) {
            Toaster.showToastCenter(this, "无法获取书籍信息");
            return;
        }
//        if (book.getSource(this).getFetchType() == 1) {
//            showWebView();
//        } else {
//            getContentOneBy(sectionUrl);
//        }
    }

    /**
     * 获取正文内容
     *
     * @param sectionUrl
     */
    private void getContentOneBy(String sectionUrl) {
        recyclerView.setVisibility(View.VISIBLE);
//        touchView.setVisibility(View.VISIBLE);
//        SearchTool.getInstance().getContent(this, sectionUrl, book.getSource(this))
//                .subscribe(new Observer<List<BookContent>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(List<BookContent> bookContent) {
//                        if (bookContent.size() > 0) {
//                            list.addAll(bookContent);
//                            contentAdapter.notifyDataSetChanged();
//                            //根据源的内容展示形式，来获取内容
//                            if (book.getSource(ContentActivity.this).getScrollPageType() == 0 && StringTools.isNotEmpty(bookContent.get(0).getNextContenUrl())) {
//                                getContentOneBy(bookContent.get(0).getNextContenUrl());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
    }


    //监听网页内容
    public class InJavaScriptLocalObj {
        WebViewCallback webViewCallback;

        public InJavaScriptLocalObj(WebViewCallback webViewCallback) {
            this.webViewCallback = webViewCallback;
        }

        @JavascriptInterface
        public void showSource(String html) {
            webViewCallback.callback(html);
        }
    }

    //回调webview网页内容
    public interface WebViewCallback {
        void callback(String html);
    }

    private void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(new WebViewCallback() {
            @Override
            public void callback(String html) {
                LogUtils.H(html);
            }
        }), "local_obj");

        // 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放
        mWebView.getSettings().setSupportZoom(true);

        // 设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        mWebView.getSettings().setBuiltInZoomControls(true);

        // 设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        mWebView.getSettings().setDomStorageEnabled(true);

        // 触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        mWebView.requestFocus();

        // 设置此属性,可任意比例缩放,设置webview推荐使用的窗口
        mWebView.getSettings().setUseWideViewPort(true);

        // 设置webview加载的页面的模式,缩放至屏幕的大小
        mWebView.getSettings().setLoadWithOverviewMode(true);


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 在开始加载网页时会回调
//                view.loadUrl(url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 在结束加载网页时会回调

                // 获取页面内容
                super.onPageFinished(view, url);
                LogUtils.H(mWebView.getProgress() + "%");
                if (mWebView.getProgress() == 100) {
                    view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                // 在每一次请求资源时，都会通过这个函数来回调
                return super.shouldInterceptRequest(view, request);
            }

        });
        mWebView.loadUrl(sectionUrl);
    }


    @Override
    public void finish() {
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
        super.finish();
    }
}
