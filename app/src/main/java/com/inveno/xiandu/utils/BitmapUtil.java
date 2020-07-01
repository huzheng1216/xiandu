package com.inveno.xiandu.utils;

import android.graphics.Bitmap;

/**
 * @author yongji.wang
 * @date 2020/7/1 9:52
 * @更新说明：
 * @更新时间：
 * @Version：
 */
public class BitmapUtil {

    /**
     * 裁剪图片
     * @param bm 图片
     * @param x 裁剪初始坐标点
     * @param y
     * @param width 宽度
     * @param height 高度
     * @return
     */
    public static Bitmap cutBitmap(Bitmap bm, int x, int y, int width, int height){
        Bitmap bitmap = null;
        if(bm!=null){
            bitmap = Bitmap.createBitmap(bm,x,y,width,height); //对图片的高度的一半进行裁剪
        }
        return bitmap;
    }
}
