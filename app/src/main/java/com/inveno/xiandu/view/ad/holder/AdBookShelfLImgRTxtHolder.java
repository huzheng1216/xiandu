package com.inveno.xiandu.view.ad.holder;

import android.content.Context;
import android.view.View;

import com.inveno.xiandu.R;

import static com.inveno.android.ad.config.AdViewType.AD_BOOK_SHELF_TYPE;
import static com.inveno.android.ad.config.AdViewType.AD_BOY_GIRL_BOTTOM_TYPE;

/**
 * 男生/女生
 */
public class AdBookShelfLImgRTxtHolder extends NormalAdViewHolder {

    public static NormalAdViewHolder create(Context context) {
        return new AdBookShelfLImgRTxtHolder(View.inflate(context, R.layout.ad_bookshelf_l_img_r_text_layout,null));
    }

    public AdBookShelfLImgRTxtHolder(View itemView) {
        super(itemView);
                child_rl = itemView.findViewById(R.id.ad_bg_r_text_l_img_rl_id);
        tv_ad_title = itemView.findViewById(R.id.ad_r_text_id);
//        rl_ad_content = itemView.findViewById(R.id.rl_ad_content);
        iv_ad_iamge = itemView.findViewById(R.id.ad_l_img_id);
//        iv_ad_logo = itemView.findViewById(R.id.iv_ad_logo);
        tv_ad_desc = itemView.findViewById(R.id.tv_ad_desc);
        holderViewType = AD_BOOK_SHELF_TYPE;
    }

    @Override
    public void onBindViewHolder(Context context, Object adValue, int position) {
        super.onBindViewHolder(context, adValue, position);
    }
}
