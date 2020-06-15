package com.inveno.xiandu.view.ad;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;

public class ADViewHolderFactory {
    public static RecyclerView.ViewHolder create(Context context,int viewType) {
        return NormalAdViewHolder.create(context);
    }
}
