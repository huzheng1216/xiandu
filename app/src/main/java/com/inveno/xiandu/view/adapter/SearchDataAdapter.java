package com.inveno.xiandu.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.bean.book.RecommendName;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.view.holder.BaseHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/15 19:11
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class SearchDataAdapter extends RecyclerBaseAdapter {

    private Context mContext;
    public Activity mActivity;
    private SearchDataAdapter.OnItemClickListener mListener;
    private String footerStr = "正在努力加载...";
    private boolean isNotMore = false;


    public SearchDataAdapter(Context context, Activity activity, ArrayList<BaseDataBean> dataList) {
        mContext = context;
        mActivity = activity;
        mDataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    protected View getHeaderView() {
        return null;
    }

    @Override
    protected View getFooterView() {
        return getViewHolderView(mContext, R.layout.item_white_load_more);
    }


    public void setDataList(ArrayList<BaseDataBean> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void setFooterText(String footerStr) {
        this.footerStr = footerStr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createViewHoder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int dataPosition = position;
        if (getHeaderView() != null && position > 0) {
            dataPosition = position - 1;
        }
        if (holder instanceof SearchDataAdapter.DefaulRemmendViewHolder) {
            if (mDataList.size() > dataPosition) {
                ((DefaulRemmendViewHolder) holder).setData(mContext, mDataList.get(dataPosition));
                int finalDataPosition = dataPosition;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    public void setNotDataFooter() {
        isNotMore = true;
        notifyItemChanged(getItemCount() - 1);
    }

    public boolean isNotMore() {
        return isNotMore;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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

    private RecyclerView.ViewHolder createViewHoder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mVIewHoder = null;

        if (viewType == HEADER_ITEM_TYPE) {
            mVIewHoder = new SearchDataAdapter.HeaderViewHolder(getHeaderView());
        } else if (viewType == RecyclerBaseAdapter.CENTER_TITLE) {
            View view = getViewHolderView(parent.getContext(), R.layout.recommend_text);
            mVIewHoder = new SearchDataAdapter.CenterTItleViewHolder(view);
        } else if (viewType == BIG_IMAGE) {
            View view = getViewHolderView(parent.getContext(), R.layout.item_big_image);
            mVIewHoder = new SearchDataAdapter.BigImageViewHolder(view);
        } else if (viewType == MORE_IMAGE) {
            View view = getViewHolderView(parent.getContext(), R.layout.item_more_image);
            mVIewHoder = new SearchDataAdapter.MoreImageViewHolder(view);
        } else if (viewType == SMALL_IMAGE) {
            View view = getView(parent);
            mVIewHoder = new SearchDataAdapter.SmallImageViewHolder(view);
        } else if (viewType == NOT_IMAGE) {
            View view = getViewHolderView(parent.getContext(), R.layout.item_not_image);
            mVIewHoder = new SearchDataAdapter.NotImageViewHolder(view);
        } else if (viewType == DEFAUL_RECOMMEND) {
            View view = getViewHolderView(parent.getContext(), R.layout.item_defaul_recommend);
            mVIewHoder = new SearchDataAdapter.DefaulRemmendViewHolder(view);
        } else if (viewType == FOOTER_ITEM_TYPE) {
            mVIewHoder = new SearchDataAdapter.FooterViewHolder(getFooterView());
        } else {
            View view = getViewHolderView(parent.getContext(), R.layout.item_defaul_recommend);
            mVIewHoder = new SearchDataAdapter.DefaulRemmendViewHolder(view);
        }
        return mVIewHoder;
    }

    @NotNull
    private View getViewHolderView(Context context, int p) {
        View view = LayoutInflater.from(context).inflate(p, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return view;
    }

    @NotNull
    private View getView(ViewGroup parent) {
        View view = getViewHolderView(parent.getContext(), R.layout.item_small_image);
        return view;
    }

    public static class HeaderViewHolder extends BaseHolder {

        TextView classify;
        TextView rankiing;
        TextView the_end;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            classify = itemView.findViewById(R.id.classify);
            rankiing = itemView.findViewById(R.id.rankiing);
            the_end = itemView.findViewById(R.id.the_end);

        }

        @Override
        public void setData(Context context, Object object) {

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

    public static class CenterTItleViewHolder extends BaseHolder<BaseDataBean> {

        TextView recommend_text;

        public CenterTItleViewHolder(@NonNull View itemView) {
            super(itemView);
            recommend_text = itemView.findViewById(R.id.recommend_text);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof RecommendName) {
                recommend_text.setText(((RecommendName) object).getRecommendName());
            }
        }
    }

    public static class BigImageViewHolder extends BaseHolder<BaseDataBean> {

        View mItemView;
        TextView big_image_title;
        TextView big_image_book_name;
        ImageView big_image_img;

        public BigImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            big_image_title = itemView.findViewById(R.id.big_image_title);
            big_image_book_name = itemView.findViewById(R.id.big_image_book_name);
            big_image_img = itemView.findViewById(R.id.big_image_img);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof EditorRecommend) {
                big_image_title.setText(((EditorRecommend) object).getTitle());
                big_image_book_name.setText(String.format("《%s》", ((EditorRecommend) object).getBook_namen()));
                ArrayList<String> imgUrls = ((EditorRecommend) object).getList_images();
                if (!imgUrls.isEmpty()) {
                    GlideUtils.LoadImage(context, imgUrls.get(0), big_image_img);
                }
            }
        }
    }

    public static class MoreImageViewHolder extends BaseHolder<BaseDataBean> {

        View mItemView;
        TextView more_image_title;
        TextView more_image_book_name;
        ImageView more_image_img_1;
        ImageView more_image_img_2;
        ImageView more_image_img_3;

        public MoreImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            more_image_title = itemView.findViewById(R.id.more_image_title);
            more_image_book_name = itemView.findViewById(R.id.more_image_book_name);
            more_image_img_1 = itemView.findViewById(R.id.more_image_img_1);
            more_image_img_2 = itemView.findViewById(R.id.more_image_img_2);
            more_image_img_3 = itemView.findViewById(R.id.more_image_img_3);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof EditorRecommend) {
                more_image_title.setText(((EditorRecommend) object).getTitle());
                more_image_book_name.setText(String.format("《%s》", ((EditorRecommend) object).getBook_namen()));
                ArrayList<String> imgUrls = ((EditorRecommend) object).getList_images();
                if (imgUrls.size() > 2) {
                    GlideUtils.LoadImage(context, imgUrls.get(0), more_image_img_1);
                    GlideUtils.LoadImage(context, imgUrls.get(1), more_image_img_2);
                    GlideUtils.LoadImage(context, imgUrls.get(2), more_image_img_3);
                }
            }
        }
    }

    public static class SmallImageViewHolder extends BaseHolder<BaseDataBean> {
        View mItemView;
        TextView small_image_title;
        TextView small_image_book_name;
        ImageView small_image_img;

        public SmallImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            small_image_title = itemView.findViewById(R.id.small_image_title);
            small_image_book_name = itemView.findViewById(R.id.small_image_book_name);
            small_image_img = itemView.findViewById(R.id.small_image_img);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof EditorRecommend) {
                small_image_title.setText(((EditorRecommend) object).getTitle());
                small_image_book_name.setText(String.format("《%s》", ((EditorRecommend) object).getBook_namen()));
                ArrayList<String> imgUrls = ((EditorRecommend) object).getList_images();
                if (!imgUrls.isEmpty()) {
                    GlideUtils.LoadImage(context, imgUrls.get(0), small_image_img);
                }
            }
        }
    }

    public static class NotImageViewHolder extends BaseHolder<BaseDataBean> {
        View mItemView;
        TextView not_image_title;
        TextView not_image_message;

        public NotImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            not_image_title = itemView.findViewById(R.id.not_image_title);
            not_image_message = itemView.findViewById(R.id.not_image_message);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof EditorRecommend) {
                not_image_title.setText(((EditorRecommend) object).getTitle());
                not_image_message.setText(((EditorRecommend) object).getBook_namen());
            }
        }
    }

    public static class DefaulRemmendViewHolder extends BaseHolder<BaseDataBean> {

        View mItemView;
        TextView default_image_book_name;
        TextView default_image_book_msg;
        TextView default_image_type;
        TextView default_image_words;
        TextView default_image_score;
        ImageView default_image_img;

        public DefaulRemmendViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            default_image_book_name = itemView.findViewById(R.id.default_image_book_name);
            default_image_book_msg = itemView.findViewById(R.id.default_image_book_msg);
            default_image_type = itemView.findViewById(R.id.default_image_type);
            default_image_words = itemView.findViewById(R.id.default_image_words);
            default_image_score = itemView.findViewById(R.id.default_image_score);
            default_image_img = itemView.findViewById(R.id.default_image_img);
        }

        @Override
        public void setData(Context context, BaseDataBean object) {
            if (object instanceof BookShelf) {
                String bookStatusStr;
                BookShelf bookShelf = ((BookShelf) object);
                default_image_book_name.setText(bookShelf.getBook_name());
                default_image_book_msg.setText(bookShelf.getIntroduction());
                default_image_type.setText(bookShelf.getCategory_name());
                if (bookShelf.getBook_status() == 0) {
                    bookStatusStr = "连载中";
                } else {
                    bookStatusStr = "已完结";
                }
                String wordsCountStr;
                if (bookShelf.getWord_count() < 1000) {
                    wordsCountStr = String.format("%s字·" + bookStatusStr, bookShelf.getWord_count());
                } else if (bookShelf.getWord_count() > 1000 && bookShelf.getWord_count() < 10000) {
                    wordsCountStr = String.format("%s千字·" + bookStatusStr, bookShelf.getWord_count() / 1000);
                } else {
                    wordsCountStr = String.format("%s万字·" + bookStatusStr, bookShelf.getWord_count() / 10000);
                }
                default_image_words.setText(wordsCountStr);
                default_image_score.setText(String.format("%s", bookShelf.getScore()));
                GlideUtils.LoadImage(context, bookShelf.getPoster(), default_image_img);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseDataBean baseDataBean);
    }
}
