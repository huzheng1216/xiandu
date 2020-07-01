package com.inveno.xiandu.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.RankingData;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.holder.BaseHolder;

import java.util.ArrayList;
import java.util.List;

import static com.inveno.android.ad.config.AdViewType.*;

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
    private List<BaseDataBean> mDataList;
    private int lastChoise = 0;
    private OnItemClickListener mListener;
    private OnImpReportListener onImpReportListener;

    private String footerStr = "正在努力加载...";
    private boolean isNotMore = false;


    public RightDataAdapter(Context context, Activity activity, List<BaseDataBean> dataList) {
        mContext = context;
        mActivity = activity;
        mDataList = dataList;
    }

    public void setOnitemClickListener(OnItemClickListener onitemClickListener) {
        mListener = onitemClickListener;
    }

    public void setOnImpReportListener(OnImpReportListener onImpReportListener) {
        this.onImpReportListener = onImpReportListener;
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
            ContentViewHolder mHolder = (ContentViewHolder) holder;
            int dataPosition = position;
            if (getHeaderView() != null) {
                dataPosition = position - 1;
            }
            if (mDataList.size() > dataPosition) {
                mHolder.setData(mContext, mDataList.get(dataPosition));
                int finalDataPosition = dataPosition;
                mHolder.rightDataView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(mDataList.get(finalDataPosition));
                    }
                });
            }

        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).load_more_tv.setText(footerStr);
            if (mDataList.size() < 10 || isNotMore) {
                ((FooterViewHolder) holder).load_more_tv.setText("沒有更多数据");
            } else {
                ((FooterViewHolder) holder).load_more_tv.setText(footerStr);
            }
        } else if (holder instanceof NormalAdViewHolder) {
            int dataPosition = position;
            if (getHeaderView() != null && position > 0) {
                dataPosition = position - 1;
            }
            ((NormalAdViewHolder) holder).onBindViewHolder(mContext, ((AdModel) mDataList.get(dataPosition)).getWrapper().getAdValue(), position);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() > 0) {
            if (getHeaderView() != null && getFooterView() != null) {
                return mDataList.size() + 2;
            } else if (getHeaderView() != null || getFooterView() != null) {
                return mDataList.size() + 1;
            } else {
                return mDataList.size();
            }
        } else {
            return 0;
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

    public void setNotDataFooter() {
        isNotMore = true;
        notifyItemChanged(getItemCount() - 1);
    }

    public boolean isNotMore() {
        return isNotMore;
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

    public static class FooterViewHolder extends BaseHolder {
        View itemView;
        TextView load_more_tv;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            load_more_tv = itemView.findViewById(R.id.load_more_tv);

        }

        @Override
        public void setData(Context context, Object object) {

        }
    }

    public static class ContentViewHolder extends BaseHolder<BaseDataBean> {

        View rightDataView;
        ImageView ranking_book_pic;
        TextView ranking_book_name;
        TextView ranking_book_type;
        TextView ranking_book_ranking;
        TextView ranking_book_text;

        public ContentViewHolder(View itemView) {
            super(itemView);
            rightDataView = itemView;
            ranking_book_pic = itemView.findViewById(R.id.ranking_book_pic);
            ranking_book_name = itemView.findViewById(R.id.ranking_book_name);
            ranking_book_type = itemView.findViewById(R.id.ranking_book_type);
            ranking_book_ranking = itemView.findViewById(R.id.ranking_book_ranking);
            ranking_book_text = itemView.findViewById(R.id.ranking_book_text);
        }

        @Override
        public void setData(Context context, BaseDataBean baseDataBean) {
            if (baseDataBean instanceof RankingData) {
                RankingData rankingData = (RankingData) baseDataBean;
                ranking_book_name.setText(rankingData.getBook_name());
                ranking_book_type.setText(rankingData.getCategory_name());
                ranking_book_ranking.setText("");
                ranking_book_text.setText("");
                if (rankingData.getRank_sort() == 1) {
                    ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_one));
                } else if (rankingData.getRank_sort() == 2) {
                    ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_two));
                } else if (rankingData.getRank_sort() == 3) {
                    ranking_book_ranking.setBackground(context.getResources().getDrawable(R.drawable.ranking_three));
                } else {
                    ranking_book_ranking.setBackground(null);
                    ranking_book_ranking.setText(String.valueOf(rankingData.getRank_sort()));
                }
                GlideUtils.LoadImage(context, rankingData.getPoster(), ranking_book_pic);
            } else if (baseDataBean instanceof BookShelf) {
                BookShelf bookShelf = (BookShelf) baseDataBean;
                ranking_book_name.setText(bookShelf.getBook_name());
                ranking_book_type.setText(bookShelf.getAuthor());
                ranking_book_ranking.setTextColor(Color.parseColor("#F5A623"));
                ranking_book_ranking.setText(String.format("%s", bookShelf.getScore()));
                ranking_book_text.setText("分");
                GlideUtils.LoadImage(context, bookShelf.getPoster(), R.drawable.background_bookshelf_adapter_foot, ranking_book_pic);
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
        } else if (viewType == AD_CATEGORY_TYPE || viewType == AD_RANKING_LIST_TYPE) {
            mVIewHoder = ADViewHolderFactory.create(mContext, viewType);
        }
        return mVIewHoder;
    }

    public void setmDataList(List<BaseDataBean> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();

        if (onImpReportListener!=null){
            onImpReportListener.onImpReport();
        }
    }

    public void addAd(AdModel adModel) {
        if (adModel != null) {
            int index = adModel.getWrapper().getIndex();
            if (mDataList != null && mDataList.size() >= index) {
                mDataList.add(index, adModel);
            } else if (mDataList == null && index == 0) {
                mDataList = new ArrayList<>();
                mDataList.add(adModel);
            }
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseDataBean baseDataBean);
    }

    public interface OnImpReportListener{
        void onImpReport();
    }

    public List<BaseDataBean> getmDataList() {
        return mDataList;
    }
}
