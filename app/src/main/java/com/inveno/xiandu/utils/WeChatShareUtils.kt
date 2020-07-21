package com.inveno.xiandu.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.wxapi.EntryActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream
import java.io.FileInputStream


/**
 * @author yongji.wang
 * @date 2020/7/16
 * @更新说明：
 * @更新时间：2020/7/16
 * @Version：1.0.0
 */
object WeChatShareUtils{
    /**
     * 分享网页类型至微信
     *
     * @param context 上下文
     * @param webUrl  网页的url
     * @param title   网页标题
     * @param content 网页描述
     * @param bitmap  位图
     */
    fun shareWeb(context: Context, webUrl: String, title: String, content: String, bitmap: Bitmap, isFriend: Boolean) {
        // 通过appId得到IWXAPI这个对象
        val wxapi = WXAPIFactory.createWXAPI(context, getAppId());
        // 检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled) {
            Toaster.showToastShort(context, "您还没有安装微信")
            return
        }

        // 初始化一个WXWebpageObject对象
        val webpageObject = WXWebpageObject();
        // 填写网页的url
        webpageObject.webpageUrl = webUrl;

        // 用WXWebpageObject对象初始化一个WXMediaMessage对象
        val msg = WXMediaMessage(webpageObject);
        // 填写网页标题、描述、位图
        msg.title = title;
        msg.description = content;
        // 如果没有位图，可以传null，会显示默认的图片
        msg.setThumbImage(bitmap);

        // 构造一个Req
        val req = SendMessageToWX.Req();
        // transaction用于唯一标识一个请求（可自定义）
        req.transaction = "webpage";
        // 上文的WXMediaMessage对象
        req.message = msg;
        // SendMessageToWX.Req.WXSceneSession是分享到好友会话
        // SendMessageToWX.Req.WXSceneTimeline是分享到朋友圈
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        // 向微信发送请求
        wxapi.sendReq(req);
    }
    /**
     * 微信分享图片
     */
    fun shareImageToWeiXin(context: Context, activity: Activity, imagePath: String, isFriend: Boolean) {
        // 通过appId得到IWXAPI这个对象
        val wxapi = WXAPIFactory.createWXAPI(context, getAppId())
        wxapi.registerApp(getAppId())
        // 检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled) {
            Toaster.showToastShort(context, "您还没有安装微信")
            return
        }

        val fis = FileInputStream(imagePath)
        val bitmap = BitmapFactory.decodeStream(fis)
        val imgObj = WXImageObject()
        imgObj.imagePath = imagePath
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj

        //设置略缩图
        val thumbBmp = Bitmap.createScaledBitmap(bitmap, 60, 60, true)
        bitmap.recycle()
        msg.thumbData = Bitmap2Bytes(thumbBmp)

        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        wxapi.sendReq(req)
    }

    /**
     * 微信分享图片
     */
    fun shareImageToWeiXin(context: Context, wxapi: IWXAPI, bmp: Bitmap, isFriend: Boolean) {
        // 检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled) {
            Toaster.showToastShort(context, "您还没有安装微信")
            return
        }

        //初始化 WXImageObject 和 WXMediaMessage 对象
        val imgObj =WXImageObject(bmp)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj;

        //设置缩略图
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 60, 60, true);
        bmp.recycle();
        msg.thumbData = Bitmap2Bytes(thumbBmp)

        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        //调用api接口，发送数据到微信
        wxapi.sendReq(req)
    }

    /**
     * 把Bitmap转Byte
     */
    fun Bitmap2Bytes(bm: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }
    /**
     * 获取微信appid
     */
    fun getAppId(): String? {
        return BuildConfig.WeChatAppID
    }
}