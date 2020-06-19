package com.inveno.xiandu.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.inveno.xiandu.R;

import java.io.File;
import java.security.MessageDigest;

/**
 * @author yongji.wang
 * @date 2020/6/9 14:19
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class GlideUtils {


    /**
     * 加载网络图片
     *
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadImage(Context mContext, String path,
                                 ImageView imageview) {
        Glide.with(mContext).load(path).centerCrop().placeholder(R.drawable.background_bookshelf_adapter_foot)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageview);
    }

    /**
     * 加载网络图片
     *
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadImage(Context mContext, String path, int dafaulImg, ImageView imageview) {
        if (dafaulImg != 0) {
            Glide.with(mContext).load(path).centerCrop().placeholder(dafaulImg).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageview);
        }else{
            Glide.with(mContext).load(path).centerCrop().placeholder(R.drawable.ic_header_default)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageview);
        }
    }

    /**
     * 加载带尺寸的图片
     *
     * @param mContext
     * @param path
     * @param Width
     * @param Height
     * @param imageview
     */
    public static void LoadImageWithSize(Context mContext, String path,
                                         int Width, int Height, ImageView imageview) {
        Glide.with(mContext).load(path).override(Width, Height)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageview);
    }

    /**
     * 加载本地图片
     *
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadImageWithLocation(Context mContext, Integer path,
                                             ImageView imageview) {
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageview);
    }

    /**
     * 圆形加载,本地资源
     */
    public static void LoadCircleImage(Context mContext, int path,
                                       ImageView imageview) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_test_ad)
                .transform(new GlideCircleTransform(mContext, 2, mContext.getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(mContext).load(path).apply(options).into(imageview);
    }

    /**
     * 圆形加载，网络图片
     */
    public static void LoadCircleImage(Context mContext, String url,
                                       ImageView imageview) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_test_ad)
                .transform(new GlideCircleTransform(mContext, 2, mContext.getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(mContext).load(url).apply(options).into(imageview);
    }

    /**
     * 圆形加载，本地文件
     */
    public static void LoadCircleImage(Context mContext, File file,
                                       ImageView imageview) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_test_ad)
                .transform(new GlideCircleTransform(mContext, 2, mContext.getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(mContext).load(file).apply(options).into(imageview);
    }

    static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
        }

        public GlideCircleTransform(Context context, float borderWidth, int borderColor) {
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }
}
