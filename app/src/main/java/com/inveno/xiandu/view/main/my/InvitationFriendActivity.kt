package com.inveno.xiandu.view.main.my

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.R
import com.inveno.xiandu.bean.welfare.InvideBean
import com.inveno.xiandu.config.ARouterPath
import com.inveno.xiandu.invenohttp.instancecontext.APIContext
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import com.inveno.xiandu.utils.LogUtils
import com.inveno.xiandu.utils.SPUtils
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.utils.WeChatShareUtils
import com.inveno.xiandu.view.TitleBarBaseActivity
import com.inveno.xiandu.view.custom.ShareView
import com.inveno.xiandu.wxapi.EntryActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_mine_invitation_friend.*

/**
 * @author yongji.wang
 * @date 2020/7/15
 * @更新说明：
 * @更新时间：2020/7/15
 * @Version：1.0.0
 */
class InvitationFriendActivity : TitleBarBaseActivity(), IWXAPIEventHandler {

    companion object {
        val INVITE_URL: String = "http://218.17.116.242:49812/noval/index.html"
//        val INVITE_URL:String = "http://218.17.116.242:49812/noval/share.html?invitecode=123"
    }

    lateinit var popView: View
    lateinit var popupWindow: PopupWindow
    lateinit var mWebView: WebView
    private var face_to_facce_view: LinearLayout? = null
    private var wechat_view: LinearLayout? = null
    private var moments_view: LinearLayout? = null
    private var qq_view: LinearLayout? = null

    private var inviteCode: String = ""

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
        getInvitaCode(false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
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
        webSetting.domStorageEnabled = true;//启用dom存储(关键就是这句)，貌似网上twitter显示有问题也是这个属性没有设置的原因
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.addJavascriptInterface(InvitationJs(), "InvitationJs")
        mWebView.loadUrl(INVITE_URL)
    }

    /**
     * 获取邀请码
     */
    fun getInvitaCode(isShare: Boolean, isFriend: Boolean = true) {
        inviteCode = SPUtils.getInformain("invite_code" + ServiceContext.userService().userPid, "")
        if (TextUtils.isEmpty(inviteCode)) {
            APIContext.getWelfareApi().getInviteCode()
                    ?.onSuccess { data: InvideBean ->
                        inviteCode = data.invite_code
                        //邀请码是唯一的，获取一次就保存到本地
                        SPUtils.setInformain("invite_code" + ServiceContext.userService().userPid, data.invite_code)
                        if (isShare) {
                            share(isFriend)
                        }
                    }
                    ?.onFail { code, message ->
                        if (isShare) {
                            Toaster.showToastShort(this@InvitationFriendActivity, "获取邀请码失败")
                        }
                    }
                    ?.execute()
        }
    }

    /**
     * 初始化popupWindow
     */
    private fun initPopwindow() {
        //加载弹出框的布局
        popView = LayoutInflater.from(this).inflate(R.layout.share_pop, null)
        face_to_facce_view = popView.findViewById(R.id.face_to_facce_view)
        wechat_view = popView.findViewById(R.id.wechat_view)
        moments_view = popView.findViewById(R.id.moments_view)
        qq_view = popView.findViewById(R.id.qq_view)

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
        LogUtils.E("face_to_facce_view:" + face_to_facce_view)
        face_to_facce_view?.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //面对面分享
            val intent = Intent(this, FaceToFacaInvitaActivity::class.java)
            startActivity(intent)
        }
        wechat_view?.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //分享微信好友
            if (!TextUtils.isEmpty(inviteCode)) {
                share(true)
            } else {
                getInvitaCode(true, true)
            }

        }
        moments_view?.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //分享微信朋友圈
            if (!TextUtils.isEmpty(inviteCode)) {
                share(false)
            } else {
                getInvitaCode(true, false)
            }
        }
        qq_view?.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            //分享qq好友
        }
    }

    private fun share(isFriend: Boolean) {
        val wxapi = WXAPIFactory.createWXAPI(this, BuildConfig.WeChatAppID)
        // 通过appId得到IWXAPI这个对象
        wxapi.registerApp(BuildConfig.WeChatAppID)
        wxapi.handleIntent(intent, this)
        val bmp = createShareImg()
        if (bmp != null) {
            WeChatShareUtils.shareImageToWeiXin(this, wxapi, bmp, isFriend)
        } else {
            Toaster.showToastShort(this, "分享图片生成失败")
        }
    }

    //生成分享图片
    private fun createShareImg(): Bitmap? {
        val shareview = ShareView(this)
        shareview.setInfo(inviteCode)
        return shareview.createImage()
    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
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
        fun getPid(): Int {
            //获取公共参数
            return ServiceContext.userService().userPid
        }

        @JavascriptInterface
        fun topUpPhone() {
            //充值
            ARouter.getInstance().build(ARouterPath.ACTIVITY_COIN_TOP_UP).navigation()
        }

        @JavascriptInterface
        fun share(url: String) {
            //分享
            this@InvitationFriendActivity.runOnUiThread(Runnable {
                this@InvitationFriendActivity.showPopWindow()
            })
        }
    }

    override fun onResp(p0: BaseResp?) {

        when (p0?.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                //分享成功
                Log.i("iiii-EntryActivity", "onResp: 分享成功")
                if (EntryActivity.isAuth) {
                    EntryActivity.isAuth = false
                } else {
                    Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show()
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                //分享取消
                Log.i("iiii-EntryActivity", "onResp: 分享取消")
                Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show()
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                //分享拒绝
                Log.i("iiii-EntryActivity", "onResp: 分享拒绝")
                Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }

    }

    override fun onReq(p0: BaseReq?) {
    }
}