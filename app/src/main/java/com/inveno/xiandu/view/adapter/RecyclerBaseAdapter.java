package com.inveno.xiandu.view.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author yongji.wang
 * @date 2020/6/13 13:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public abstract class RecyclerBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int EMPTY_ITEM_TYPE = -1;

    public static final int HEADER_ITEM_TYPE = 0;
    public static final int FOOTER_ITEM_TYPE = 1;
    public static final int CONTENT_ITEM_TYPE = 2;

    public static final int AD_ITEM_TYPE_1 = 3;
    public static final int AD_ITEM_TYPE_2 = 4;
    public static final int AD_ITEM_TYPE_3 = 5;
    public static final int AD_ITEM_TYPE_4 = 6;

    protected View headerView;
    protected View footerView;

    protected abstract View getHeaderView();

    protected abstract View getFooterView();
}
