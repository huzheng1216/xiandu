package com.inveno.xiandu.view.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.inveno.xiandu.R


/**
 * @author yongji.wang
 * @date 2020/7/15
 * @更新说明：
 * @更新时间：2020/7/15
 * @Version：1.0.0
 */
class ShareView(context: Context) : FrameLayout(context) {

    private val IMAGE_WIDTH = 1080
    private val IMAGE_HEIGHT = 1920

    private var share_invite_code: TextView? = null

    init {
        init()
    }

    private fun init() {
        val layout: View = View.inflate(context, R.layout.share_qrcode_layout, this)
        share_invite_code = layout.findViewById(R.id.share_invite_code)
    }

    /**
     * 设置相关信息
     *
     * @param info
     */
    fun setInfo(info: String?) {
        share_invite_code!!.text = info
    }

    /**
     * 生成图片
     *
     * @return
     */
    fun createImage(): Bitmap? {
        //由于直接new出来的view是不会走测量、布局、绘制的方法的，所以需要我们手动去调这些方法，不然生成的图片就是黑色的。
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, MeasureSpec.EXACTLY)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, MeasureSpec.EXACTLY)
        measure(widthMeasureSpec, heightMeasureSpec)
        layout(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
        val bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
