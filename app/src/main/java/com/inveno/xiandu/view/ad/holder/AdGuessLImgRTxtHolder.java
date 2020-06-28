package com.inveno.xiandu.view.ad.holder;

import android.content.Context;
import android.view.View;

import com.inveno.xiandu.R;

import static com.inveno.android.ad.config.AdViewType.AD_BOY_GIRL_BOTTOM_TYPE;
import static com.inveno.android.ad.config.AdViewType.AD_GUESS_YOU_LIKE_TYPE_1;

/**
 * 男生/女生
 */
public class AdGuessLImgRTxtHolder extends NormalAdViewHolder {

    public static NormalAdViewHolder create(Context context) {
        return new AdGuessLImgRTxtHolder(View.inflate(context, R.layout.ad_guess_l_img_r_text_layout,null));
    }

    public AdGuessLImgRTxtHolder(View itemView) {
        super(itemView);
                child_rl = itemView.findViewById(R.id.ad_bg_r_text_l_img_rl_id);
        tv_ad_title = itemView.findViewById(R.id.ad_r_text_id);
//        rl_ad_content = itemView.findViewById(R.id.rl_ad_content);
        iv_ad_iamge = itemView.findViewById(R.id.ad_l_img_id);
//        iv_ad_logo = itemView.findViewById(R.id.iv_ad_logo);
//        tv_ad_desc = itemView.findViewById(R.id.tv_ad_desc);
        holderViewType = AD_GUESS_YOU_LIKE_TYPE_1;
    }

    @Override
    public void onBindViewHolder(Context context, Object adValue, int position) {
        super.onBindViewHolder(context, adValue, position);
    }
}
