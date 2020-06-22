package com.inveno.xiandu.view.ad.holder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.donews.b.main.info.DoNewsAdNativeData;
import com.donews.b.main.info.NativeAdListener;
import com.inveno.xiandu.R;

import java.util.ArrayList;
import java.util.List;

public class NormalLeftImgRightTextADViewHolder extends RecyclerView.ViewHolder {
    public static NormalLeftImgRightTextADViewHolder create(Context context) {
        return new NormalLeftImgRightTextADViewHolder(View.inflate(context, R.layout.ad_bg_r_text_l_img_layout,null));
    }

    RelativeLayout child_rl;
    TextView tv_ad_title;//标题
    TextView tv_ad_desc;//描述
    FrameLayout rl_ad_content;
    ImageView iv_ad_iamge;//背景图片
    ImageView iv_ad_logo;//logo

    public NormalLeftImgRightTextADViewHolder(View itemView) {
        super(itemView);
        // R.layout.ad_normal_item_layout
        tv_ad_title = itemView.findViewById(R.id.tv_ad_title);
        iv_ad_iamge = itemView.findViewById(R.id.iv_ad_image);
        iv_ad_logo = itemView.findViewById(R.id.iv_ad_logo);
        tv_ad_desc = itemView.findViewById(R.id.tv_ad_desc);
    }

    public void onBindViewHolder(Context context,Object adValue,int position) {
        if(adValue instanceof DoNewsAdNativeData){
            final DoNewsAdNativeData doNewsAdNativeData= (DoNewsAdNativeData) adValue;
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(child_rl);
            tv_ad_title.setText(doNewsAdNativeData.getTitle());
            tv_ad_desc.setText(doNewsAdNativeData.getDese());
            if (null != doNewsAdNativeData.getImgUrl() && !"".equals(doNewsAdNativeData.getImgUrl())) {//如果返回的大图不为null
                Glide.with(context).load(doNewsAdNativeData.getImgUrl()).into(iv_ad_iamge);
            } else {//如果没有返回大图，则用小图渲染
                if (null != doNewsAdNativeData.getImgList()) {
                    Glide.with(context).load(doNewsAdNativeData.getImgList().get(0)).into(iv_ad_iamge);
                }
            }
            doNewsAdNativeData.bindView(context, child_rl, null, clickableViews, new NativeAdListener() {
                @Override
                public void onADExposed() {
                }

                @Override
                public void onADClicked() {
                }
            });
            iv_ad_logo.setVisibility(View.VISIBLE);
            Glide.with(context).load(doNewsAdNativeData.getLogoUrl()).into(iv_ad_logo);
            //如果非广点通的，需要添加点击事件和广告标识 强烈建议这样处理，体验曝光都很棒
            if (doNewsAdNativeData.getAdFrom() != 5) {
                doNewsAdNativeData.onADExposed(child_rl);//曝光上报
                child_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doNewsAdNativeData.onADClicked(child_rl);//点击上报
                    }
                });
            }
            //设置一个标识，用来item点击事件获取数据
            itemView.setTag(position);
        }

    }
}
