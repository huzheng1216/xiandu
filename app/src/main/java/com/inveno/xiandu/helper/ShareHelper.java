package com.inveno.xiandu.helper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.view.custom.MyPopWindow;

public class ShareHelper implements View.OnClickListener
{
    
    private View mSharePopView;
    private MyPopWindow mSharePopWindow;
    private Context mContext;
    private String mRequestShareUrl;
    public static final int SCENE_SESSION = 0;
    public static final int SCENE_TIMELINE = 1;
    
    public ShareHelper(Context context)
    {
        mContext = context;
    }
    
    //分享 社区、微信、朋友圈
    public void showSharePop(int orientation)
    {
        if (mSharePopWindow == null)
        {
            MyPopWindow.Builder builder = new MyPopWindow.Builder(mContext)
                    .setHideStateBar(false);
            if (orientation == Configuration
                    .ORIENTATION_LANDSCAPE)
            {//横屏弹窗样式
                if (mSharePopView == null)
                    //针对三星这个机型做另外处理，因为这里不知为何切换到横屏，getResources().getConfiguration().orientation得到的还是竖屏的参数，应用的还是竖屏的布局！
                    // （播放器的弹窗不会有这个问题 这边先简单粗暴处理了，不再去理原来的逻辑）
                    mSharePopView = View.inflate(mContext, Build.MODEL.equalsIgnoreCase
                            ("SM-G9209") ? R.layout.share_pop_sm_land : R.layout.share_pop, null);
                builder.setAnimStyle(R.style.pop_right_anim_style)
                        .setWidth(DensityUtil.dip2px(mContext, 253))
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setOrientation(Gravity.RIGHT);
            }
            else
            {
                if (mSharePopView == null)
                    mSharePopView = View.inflate(mContext, R.layout.share_pop, null);
                builder.setAnimStyle(R.style.pop_anim_style)
                        .setHeight(DensityUtil.dip2px(mContext, 180))
                        // .utils.DeviceUtils.dip2px(mContext, 330)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setOrientation(Gravity.BOTTOM);
            }
            mSharePopWindow = builder.setLayoutView(mSharePopView).build();
        }
        bindShareViewEvent(mSharePopView);
        mSharePopWindow.show();
        
    }
    
    public void destroyPopView()
    {
        if (mSharePopWindow != null)
        {
            mSharePopView = null;
            mSharePopWindow.destroy();
            mSharePopWindow = null;
            
        }
    }
    
    //分享弹窗的点击事件
    private void bindShareViewEvent(View popView)
    {
        View face_to_facce_view = popView.findViewById(R.id.face_to_facce_view);
        View wechatView = popView.findViewById(R.id.wechat_view);
        View momentsView = popView.findViewById(R.id.moments_view);
//        View cancelTv = popView.findViewById(R.id.cancel_tv);

        face_to_facce_view.setOnClickListener(this);
        wechatView.setOnClickListener(this);
        momentsView.setOnClickListener(this);
//        if (cancelTv != null)
//            cancelTv.setOnClickListener(this);
        
    }
    
    //分享到社区
    private void face2faceShare()
    {
        //面对面分享
    }
    
    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.face_to_facce_view)
        {
            face2faceShare();
        }
        else if (v.getId() == R.id.wechat_view)
        {
            requestShareInfo(SCENE_SESSION);
        }
        else if (v.getId() == R.id.moments_view)
        {
            requestShareInfo(SCENE_TIMELINE);
        }
       /* else if (v.getId() == R.id.cancel_tv)
        {
        
        }*/
        if (mSharePopView != null)
            mSharePopWindow.dismiss();
    }
    
    private void requestShareInfo(final int scene)
    {
        //先生成分享图片，在进行分享
        callNativeWechatPlugin(scene);
    }
    
    private void callNativeWechatPlugin(int scene)
    {
        //这里进行图片分享
    }
    
}
