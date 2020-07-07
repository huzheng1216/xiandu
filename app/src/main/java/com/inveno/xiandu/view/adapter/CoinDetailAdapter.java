package com.inveno.xiandu.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.coin.CoinDetailData;
import com.inveno.xiandu.utils.DateTimeUtils;
import com.inveno.xiandu.view.holder.BaseHolder;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/19 14:32
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class CoinDetailAdapter extends RecyclerBaseAdapter {

    private Context mContext;
    public Activity mActivity;
    private String footerStr = "正在努力加载...";

    public CoinDetailAdapter(Context context, Activity activity, ArrayList<BaseDataBean> dataList) {
        mContext = context;
        mActivity = activity;
        mDataList = dataList;
    }

    @Override
    protected View getHeaderView() {
        return null;
    }

    @Override
    protected View getFooterView() {
        return getViewHolderView(mContext, R.layout.item_load_more);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mVIewHoder = createDataViewHolder(parent, viewType);
        if (mVIewHoder != null) {
            return mVIewHoder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_detail, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ContentViewHolder(view);
        }
    }

    public void setFooterText(String footerStr) {
        this.footerStr = footerStr;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof ContentViewHolder) {
            ((ContentViewHolder) holder).setData(mContext, mDataList.get(position));

        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).load_more_tv.setText(footerStr);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.size() == 0) {
            return EMPTY_ITEM_TYPE;
        } else {
            if (getFooterView() != null && position == getItemCount() - 1) {
                return FOOTER_ITEM_TYPE;
            }
            if (getHeaderView() != null && position == 0) {
                return HEADER_ITEM_TYPE;
            }
            return CONTENT_ITEM_TYPE;
        }
    }

    public void setData(ArrayList<BaseDataBean> baseDataBeans) {
        this.mDataList = baseDataBeans;
        notifyDataSetChanged();
    }

    private View getViewHolderView(Context context, int p) {
        View view = LayoutInflater.from(context).inflate(p, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return view;
    }


    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mVIewHoder = null;
        if (viewType == HEADER_ITEM_TYPE) {
            mVIewHoder = new HeaderViewHolder(getHeaderView());
        } else if (viewType == CONTENT_ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_detail, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            mVIewHoder = new ContentViewHolder(view);

        } else if (viewType == FOOTER_ITEM_TYPE) {
            mVIewHoder = new FooterViewHolder(getFooterView());
        }
        return mVIewHoder;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView load_more_tv;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            load_more_tv = itemView.findViewById(R.id.load_more_tv);

        }

    }

    public static class ContentViewHolder extends BaseHolder<BaseDataBean> {

        View rightDataView;
        TextView coin_detail_name;
        TextView coin_detail_time;
        TextView coin_detail_change;

        public ContentViewHolder(View itemView) {
            super(itemView);
            rightDataView = itemView;
            coin_detail_name = itemView.findViewById(R.id.coin_detail_name);
            coin_detail_time = itemView.findViewById(R.id.coin_detail_time);
            coin_detail_change = itemView.findViewById(R.id.coin_detail_change);
        }

        @Override
        public void setData(Context context, BaseDataBean baseDataBean) {
            if (baseDataBean instanceof CoinDetailData) {
                CoinDetailData coinDetailData = (CoinDetailData) baseDataBean;
                coin_detail_name.setText(coinDetailData.getTask_name());
                if (coinDetailData.getCoin() > 0) {
                    coin_detail_change.setText(String.format("+%s金币", coinDetailData.getCoin()));
                } else {
                    coin_detail_change.setText(String.valueOf(coinDetailData.getCoin()));
                }

                //时间戳需要转化
                coin_detail_time.setText(DateTimeUtils.getShowFormat(coinDetailData.getTask_time()));
            }
        }
    }
}
