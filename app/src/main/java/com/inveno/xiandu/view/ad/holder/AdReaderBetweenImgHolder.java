package com.inveno.xiandu.view.ad.holder;

import android.content.Context;
import android.view.View;

import com.inveno.xiandu.R;

import static com.inveno.android.ad.config.AdViewType.AD_EDITOR_RECOMMEND_TYPE_2;

/**
 * 男生/女生
 */
public class AdReaderBetweenImgHolder extends NormalAdViewHolder {

    public static NormalAdViewHolder create(Context context) {
        return new AdReaderBetweenImgHolder(View.inflate(context, R.layout.ad_readerbetween_big_img_layout,null));
    }

    public AdReaderBetweenImgHolder(View itemView) {
        super(itemView);
        child_rl = itemView.findViewById(R.id.ad_l_text_r_img_rl_id);
        tv_ad_title = itemView.findViewById(R.id.ad_l_text_id);
//        rl_ad_content = itemView.findViewById(R.id.rl_ad_content);
        iv_ad_iamge = itemView.findViewById(R.id.ad_r_img_id);
//        iv_ad_logo = itemView.findViewById(R.id.iv_ad_logo);
//        tv_ad_desc = itemView.findViewById(R.id.tv_ad_desc);
        holderViewType = AD_EDITOR_RECOMMEND_TYPE_2;
    }

    @Override
    public void onBindViewHolder(Context context, Object adValue, int position) {
        super.onBindViewHolder(context, adValue, position);
    }
}
