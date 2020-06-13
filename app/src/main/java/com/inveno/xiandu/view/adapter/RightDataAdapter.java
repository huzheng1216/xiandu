package com.inveno.xiandu.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.store.RankingBean;
import com.inveno.xiandu.view.holder.BaseHolder;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/12 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RightDataAdapter extends RecyclerBaseAdapter {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<BaseDataBean> mDataList;
    private int lastChoise = 0;
    private OnItemClickListener mListener;


    public RightDataAdapter(Context context, Activity activity, ArrayList<BaseDataBean> dataList) {
        mContext = context;
        mActivity = activity;
        mDataList = dataList;
    }

    public void setOnitemClickListener(OnItemClickListener onitemClickListener) {
        mListener = onitemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.size() == 0) {
            return EMPTY_ITEM_TYPE;
        } else {
            if (getHeaderView() == null && getFooterView() == null) {
                return mDataList.get(position).getType();
            } else {
                if (getFooterView() != null && position == getItemCount() - 1) {
                    return FOOTER_ITEM_TYPE;
                }
                if (getHeaderView() != null) {
                    if (position == 0) {
                        return HEADER_ITEM_TYPE;
                    }
                    return mDataList.get(position - 1).getType();
                } else {
                    return mDataList.get(position).getType();
                }
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mVIewHoder = createDataViewHolder(parent, viewType);
        if (mVIewHoder != null) {
            return mVIewHoder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_right_data_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof ContentViewHolder) {
            int dataPosition = position;
            if (getHeaderView() != null) {
                dataPosition = position - 1;
            }
            RankingBean rankingBean = (RankingBean) mDataList.get(dataPosition);
            rankingBean.setRankingNum(dataPosition);
            ((ContentViewHolder) holder).setData(mContext, (RankingBean) mDataList.get(dataPosition));

        } else if (holder instanceof FooterViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        if (getHeaderView() != null && getFooterView() != null) {
            return mDataList.size() + 2;
        } else if (getHeaderView() != null || getFooterView() != null) {
            return mDataList.size() + 1;
        } else {
            return mDataList.size();
        }
    }

    public void setHeaderView(View view) {
        headerView = view;
        notifyItemChanged(0);
    }

    public void setFooterView(View view) {
        footerView = view;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    protected View getHeaderView() {
        return null;
    }

    @Override
    protected View getFooterView() {
        if (footerView != null) {
            return footerView;
        }
        return null;
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ContentViewHolder extends BaseHolder<RankingBean> {

        View rightDataView;
        ImageView ranking_book_pic;
        TextView ranking_book_name;
        TextView ranking_book_type;
        TextView ranking_book_ranking;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ranking_book_pic = itemView.findViewById(R.id.ranking_book_pic);
            ranking_book_name = itemView.findViewById(R.id.ranking_book_name);
            ranking_book_type = itemView.findViewById(R.id.ranking_book_type);
            ranking_book_ranking = itemView.findViewById(R.id.ranking_book_ranking);
        }

        @Override
        public void setData(Context context, RankingBean object) {
            ranking_book_name.setText(object.getRankBookname());
            ranking_book_type.setText(object.getRankBookType());
            ranking_book_ranking.setText("");
            if (object.getRankingNum() == 0) {
                ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_one));
            } else if (object.getRankingNum() == 1) {
                ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_two));
            } else if (object.getRankingNum() == 2) {
                ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_three));
            } else {
                ranking_book_ranking.setBackground(null);
                ranking_book_ranking.setText(String.valueOf(object.getRankingNum()));
            }
        }
    }

    public static class Ad1ViewHolder extends RecyclerView.ViewHolder {

        View lefMenuView;
        TextView left_menu_name;

        public Ad1ViewHolder(View itemView) {
            super(itemView);
            this.lefMenuView = itemView;
            left_menu_name = itemView.findViewById(R.id.left_menu_name);
        }
    }

    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mVIewHoder = null;
        if (viewType == HEADER_ITEM_TYPE) {
            mVIewHoder = new HeaderViewHolder(getHeaderView());
        } else if (viewType == CONTENT_ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_right_data_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            mVIewHoder = new ContentViewHolder(view);

        } else if (viewType == FOOTER_ITEM_TYPE) {
            mVIewHoder = new FooterViewHolder(getFooterView());
        }
        return mVIewHoder;
    }

    public interface OnItemClickListener {
        void onItemClick(String name);
    }
}
