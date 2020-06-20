package com.inveno.xiandu.view.ad;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.view.ad.holder.AdLTxtRImgHolder;

import static com.inveno.android.ad.config.AdViewType.*;

public class ADViewHolderFactory {
    public static RecyclerView.ViewHolder create(Context context, int viewType) {
        switch (viewType){
            case AD_EDITOR_RECOMMEND_TYPE_1:
                return AdLTxtRImgHolder.create(context);
            case AD_EDITOR_RECOMMEND_TYPE_2:
                break;
            case AD_GUESS_YOU_LIKE_TYPE_1:
                break;
            default:
                break;
        }

        return null;
    }


}

