package com.inveno.xiandu.view.main.my

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.PopupWindow
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.inveno.android.basics.service.event.EventService.Companion.post
import com.inveno.android.basics.service.third.json.JsonUtil.Companion.toJson
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.R
import com.inveno.xiandu.config.ARouterPath
import com.inveno.xiandu.invenohttp.api.user.LoginAPI
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository
import com.inveno.xiandu.view.TitleBarBaseActivity
import kotlinx.android.synthetic.main.activity_mine_invitation_friend.*
import kotlinx.android.synthetic.main.share_pop.*
import java.util.*


/**
 * @author yongji.wang
 * @date 2020/7/15
 * @更新说明：
 * @更新时间：2020/7/15
 * @Version：1.0.0
 */
class InvitationFriendActivity : TitleBarBaseActivity() {
    lateinit var popView: View
    lateinit var popupWindow: PopupWindow

    override fun layoutID(): Int {
        return R.layout.activity_mine_invitation_friend
    }

    override fun getCenterText(): String {
        return "邀请好友"
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

        val webSetting = invitation_webview.settings
        webSetting.javaScriptEnabled = true
        //不显示webview缩放按钮
        webSetting.displayZoomControls = false
        invitation_webview.addJavascriptInterface(InvitationJs(), "InvitationJs")
        invitation_webview.loadUrl("https://www.baidu.com/")
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
            //面对面分享
        }
        wechat_view.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //分享微信好友

        }
        moments_view.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //分享微信朋友圈
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

    override fun onDestroy() {
        super.onDestroy()
        invitation_webview.loadUrl("about:blank")
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
            Toaster.showToastShort(this@InvitationFriendActivity, "分享")
            this@InvitationFriendActivity.runOnUiThread(Runnable {
                this@InvitationFriendActivity.showPopWindow()
            })
        }
    }
}