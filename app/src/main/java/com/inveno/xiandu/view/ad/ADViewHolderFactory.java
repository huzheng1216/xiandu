package com.inveno.xiandu.view.ad;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.view.ad.holder.AdBGRTxtLImgHolder;
import com.inveno.xiandu.view.ad.holder.AdBookDetailLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdBookDetailPopLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdBookShelfLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdCategoryLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdFootTraceLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdGuessLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdLTxtRImgHolder;
import com.inveno.xiandu.view.ad.holder.AdRankingListLImgRTxtHolder;
import com.inveno.xiandu.view.ad.holder.AdSearchLImgRTxtHolder;

import static com.inveno.android.ad.config.AdViewType.*;

public class ADViewHolderFactory {
    public static RecyclerView.ViewHolder create(Context context, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case AD_EDITOR_RECOMMEND_TYPE_1:
                holder = AdLTxtRImgHolder.create(context);
                break;
            case AD_EDITOR_RECOMMEND_TYPE_2:

                break;
            case AD_GUESS_YOU_LIKE_TYPE_1:
                holder = AdGuessLImgRTxtHolder.create(context);
                break;
            case AD_BOY_GIRL_BOTTOM_TYPE:
                holder = AdBGRTxtLImgHolder.create(context);
                break;
            case AD_CATEGORY_TYPE:
                holder = AdCategoryLImgRTxtHolder.create(context);
                break;
            case AD_SEARCH_TYPE:
                holder = AdSearchLImgRTxtHolder.create(context);
                break;
            case AD_RANKING_LIST_TYPE:
                holder = AdRankingListLImgRTxtHolder.create(context);
                break;
            case AD_BOOK_SHELF_TYPE:
                holder = AdBookShelfLImgRTxtHolder.create(context);
                break;
            case AD_BOOK_DETAIL_TYPE:
                holder = AdBookDetailLImgRTxtHolder.create(context);
                break;
            case AD_BOOK_DETAIL_POP_TYPE:
                holder = AdBookDetailPopLImgRTxtHolder.create(context);
                break;
            case AD_READ_FOOT_TRACE_TYPE:
                holder = AdFootTraceLImgRTxtHolder.create(context);
                break;
            default:
                break;
        }

        return holder;
    }


}

