package com.inveno.xiandu.view.browser

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.alibaba.android.arouter.launcher.ARouter
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.R
import com.inveno.xiandu.config.ARouterPath
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.view.TitleBarBaseActivity
import kotlinx.android.synthetic.main.activity_mine_invitation_friend.*
import kotlinx.android.synthetic.main.share_pop.*


/**
 * @author yongji.wang
 * @date 2020/7/15
 * @更新说明：
 * @更新时间：2020/7/15
 * @Version：1.0.0
 */
class BrowserActivity : TitleBarBaseActivity() {
    lateinit var mWebView: WebView
    lateinit var popView: View
    lateinit var popupWindow: PopupWindow

    override fun layoutID(): Int {
        return R.layout.activity_mine_invitation_friend
    }

    override fun getCenterText(): String {
        return ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(R.color.white, true)
    }

    override fun initView() {
        super.initView()
        initWebView()
    }

    fun initWebView() {
        //初始化分享弹窗
        initPopwindow()

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        //创建一个LayoutParams宽高设定为全屏
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //创建WebView
        mWebView = WebView(applicationContext)
        //设置WebView的宽高
        mWebView.setLayoutParams(layoutParams)
        //把webView添加到容器中
        m_webview_layout.addView(mWebView)
        val webSetting = mWebView.settings
        webSetting.javaScriptEnabled = true
        //不显示webview缩放按钮
        webSetting.displayZoomControls = false
        //                inAppWebView.setWebChromeClient(new InAppChromeClient
        // (thatWebView));
        val client: WebViewClient = BrowserClient()
        mWebView.webViewClient = client
        mWebView.webChromeClient = BrowserChromeClient()
        mWebView.addJavascriptInterface(InvitationJs(), "InvitationJs")
        val url = intent.getStringExtra("browser_url")
        mWebView.loadUrl(url)
    }

    /**
     * 初始化popupWindow
     */
    fun initPopwindow() {
        //加载弹出框的布局
        popView = LayoutInflater.from(this).inflate(R.layout.share_pop, null)
        popupWindow = PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setFocusable(true) // 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.setOutsideTouchable(true)
        //设置可以点击
        popupWindow.setTouchable(true)
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style)
        // 按下android回退物理键 PopipWindow消失解决
    }

    fun showPopWindow() {
        //从底部显示
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0)
        //添加按键事件监听
        setButtonListeners()
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { setBackgroundAlpha(1f) })
        setBackgroundAlpha(0.5f)
    }

    private fun setButtonListeners() {
        face_to_facce_view.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        wechat_view.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        moments_view.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
    }

    inner class BrowserClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            if (view != null) {
                this@BrowserActivity.titleText.setText(view.getTitle())
            }
            super.onPageStarted(view, url, favicon)
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            if (view != null) {
                this@BrowserActivity.titleText.setText(view.getTitle())
            }
            super.onPageFinished(view, url)
        }
    }

    inner class BrowserChromeClient: WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                load_progress.visibility = View.GONE
            }else {
                if (load_progress.getVisibility() == View.GONE) {
                    load_progress.visibility = View.VISIBLE
                }
                load_progress.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }
    }


    override fun onDestroy() {
        //加载null内容
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        //清除历史记录
        mWebView.clearHistory()
        //移除WebView
        (mWebView.parent as ViewGroup).removeView(mWebView)
        //销毁VebView
        mWebView.destroy()
        super.onDestroy()
    }

    inner class InvitationJs {
        @JavascriptInterface
        fun getParam(): String {
            //获取公共参数
            return ServiceContext.bacicParamService().paramJsonStr
        }

        @JavascriptInterface
        fun topUpPhone() {
            //充值
            ARouter.getInstance().build(ARouterPath.ACTIVITY_COIN_TOP_UP).navigation()
        }

        @JavascriptInterface
        fun share(url: String) {
            //分享
            Toaster.showToastShort(this@BrowserActivity, "分享")
            this@BrowserActivity.runOnUiThread(Runnable {
                this@BrowserActivity.showPopWindow()
            })
        }
    }
}