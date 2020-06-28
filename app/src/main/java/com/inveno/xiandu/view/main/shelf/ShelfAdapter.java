package com.inveno.xiandu.view.main.shelf;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdBookModel;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.inveno.android.ad.config.AdViewType.AD_BOOK_SHELF_TYPE;

/**
 * Created By huzheng
 * Date 2020/3/5
 * Des 书架适配
 */
public class ShelfAdapter extends RecyclerBaseAdapter {

    private Context context;
    private List<Bookbrack> data;
    private ShelfAdapterListener shelfAdapterListener;

    private String headerTime;
    private String headerCoin;

    //是否在选择状态
    private boolean isSelect = false;

    public ShelfAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setShelfAdapterListener(ShelfAdapterListener shelfAdapterListener) {
        this.shelfAdapterListener = shelfAdapterListener;
    }

    public void setData(List<Bookbrack> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void setHeaderTime(String time) {
        headerTime = time;
        notifyDataSetChanged();
    }

    public void addAd(AdBookModel adBookModel) {
        if (data.size() >= adBookModel.getIndex()) {
            data.add(adBookModel.getIndex(), adBookModel);
            notifyDataSetChanged();
        }
    }

    public void setCoinNum(String coinNum) {
        headerCoin = coinNum;
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelect(true);
        }
        notifyDataSetChanged();
    }

    public void deleteSelect() {
        ArrayList<Bookbrack> bookbracks = new ArrayList<>();
        for (int i = data.size() - 1; i >= 0; i--) {
            if (data.get(i) instanceof AdBookModel) {
                continue;
            }
            if (data.get(i).isSelect()) {
                Bookbrack bookbrack = data.get(i);
                bookbracks.add(bookbrack);
                BookShelf bookShelf = SQL.getInstance().getBookShelf(bookbrack.getContent_id());
                if (bookShelf != null) {
                    SQL.getInstance().delBookShelf(bookShelf);
                    data.remove(bookbrack);
                }
            }
        }

        SQL.getInstance().delBookbrack(bookbracks);
        setSelect(false);
        notifyDataSetChanged();
        Toaster.showToastCenterShort(context, String.format("已删除%s本书", bookbracks.size()));
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
        if (!isSelect) {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelect(false);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateDataViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderView() == null && getFooterView() == null) {
            if (data.size() > position - 1 && position > 0) {
                if (data.get(position - 1) instanceof AdBookModel) {
                    return AD_BOOK_SHELF_TYPE;
                }
            }
            return CONTENT_ITEM_TYPE;
        } else {
            if (getFooterView() != null && position == getItemCount() - 1) {
                return FOOTER_ITEM_TYPE;
            }
            if (getHeaderView() != null) {
                if (position == 0) {
                    return HEADER_ITEM_TYPE;
                }
                if (data.get(position - 1) instanceof AdBookModel) {
                    return AD_BOOK_SHELF_TYPE;
                }
                return CONTENT_ITEM_TYPE;
            } else {
                if (data.get(position - 1) instanceof AdBookModel) {
                    return AD_BOOK_SHELF_TYPE;
                }
                return CONTENT_ITEM_TYPE;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shelfAdapterListener.onFooterClick();
                }
            });
            if (data.size() > 0) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }

        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (TextUtils.isEmpty(headerTime)) {
                headerViewHolder.bookrack_read_time.setText("--");
            } else {
                headerViewHolder.bookrack_read_time.setText(headerTime);
            }

            if (TextUtils.isEmpty(headerCoin)) {
                headerViewHolder.bookrack_coin_num.setText("--");
            } else {
                headerViewHolder.bookrack_coin_num.setText(headerCoin);
            }
        } else if (holder instanceof NormalAdViewHolder) {
            int realPosition = position;
            if (getHeaderView() != null) {
                realPosition = position - 1;
            }
            ((NormalAdViewHolder) holder).onBindViewHolder(context, ((AdBookModel) data.get(realPosition)).getWrapper().getAdValue(), realPosition);
        } else {

            int realPosition = position;
            if (getHeaderView() != null) {
                realPosition = position - 1;
            }
            int finalRealPosition = realPosition;
            ItemViewHolder iholder = (ItemViewHolder) holder;
            iholder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.get(finalRealPosition).setSelect(true);
                    shelfAdapterListener.onBookDelete(data.get(finalRealPosition));
                }
            });
            if (isSelect) {
                iholder.bookbrack_checkbox.setVisibility(View.VISIBLE);
                if (data.get(realPosition).isSelect()) {
                    iholder.bookbrack_checkbox.setChecked(true);
                }
                iholder.bookbrack_checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!iholder.bookbrack_checkbox.isChecked()) {
                            data.get(finalRealPosition).setSelect(false);
                        } else {
                            data.get(finalRealPosition).setSelect(true);
                        }
                    }
                });
            } else {
                iholder.bookbrack_checkbox.setVisibility(View.GONE);
            }
            iholder.adapter_bookshelf_book_name.setText(data.get(realPosition).getBook_name());
            if (TextUtils.isEmpty(data.get(realPosition).getChapter_name())) {

                iholder.adapter_bookshelf_read_name.setText("还未开始阅读");
            } else {
                iholder.adapter_bookshelf_read_name.setText(String.format("读到：%s", data.get(realPosition).getChapter_name()));
            }
            if (realPosition == 0) {
                if (!(data.get(realPosition) instanceof AdBookModel)) {
                    iholder.adapter_bookshelf_continue.setVisibility(View.VISIBLE);
                }
            } else if (realPosition == 1 && data.get(realPosition - 1) instanceof AdBookModel) {
                iholder.adapter_bookshelf_continue.setVisibility(View.VISIBLE);
            } else {
                iholder.adapter_bookshelf_continue.setVisibility(View.GONE);
            }
            iholder.adapter_bookshelf_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //继续阅读
                    shelfAdapterListener.onBookReadContinue(data.get(finalRealPosition));
                }
            });
            Glide.with(context).load(data.get(realPosition).getPoster()).into(iholder.adapter_bookshelf_book_img);

            ClickUtil.bindSingleClick(iholder.bookbrack_itemView, 500, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelect) {
                        if (shelfAdapterListener != null) {
                            shelfAdapterListener.onBookClick(data.get(finalRealPosition));
                        }
                    } else {
                        iholder.bookbrack_checkbox.setChecked(!iholder.bookbrack_checkbox.isChecked());
                        data.get(finalRealPosition).setSelect(iholder.bookbrack_checkbox.isChecked());
                    }
                }
            });
            iholder.bookbrack_itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //不再选择状态要变成选择状态
                    if (!isSelect) {
                        if (shelfAdapterListener != null) {
                            shelfAdapterListener.onBookLongClick(data.get(finalRealPosition), iholder.itemView);
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (getHeaderView() != null && getFooterView() != null) {
            return data.size() + 2;
        } else if (getHeaderView() != null || getFooterView() != null) {
            return data.size() + 1;
        } else {
            return data.size();
        }
    }

    @Override
    protected View getHeaderView() {
        if (headerView != null)
            return headerView;
        return null;
    }

    @Override
    protected View getFooterView() {
        if (footerView != null)
            return footerView;
        return null;
    }

    public void setHeaderView(View view) {
        headerView = view;
        notifyItemChanged(0);
    }

    public void setFooterView(View view) {
        footerView = view;
        notifyItemChanged(getItemCount() - 1);
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position == data.size() ? Const.ADAPTER_TYPE_FOOT : Const.ADAPTER_TYPE_DATA;
//    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        RelativeLayout bookbrack_itemView;
        ImageView adapter_bookshelf_book_img;
        TextView adapter_bookshelf_book_name;
        TextView adapter_bookshelf_read_name;
        TextView adapter_bookshelf_continue;
        CheckBox bookbrack_checkbox;
        View delete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            bookbrack_itemView = itemView.findViewById(R.id.bookbrack_itemView);
            adapter_bookshelf_book_img = itemView.findViewById(R.id.adapter_bookshelf_book_img);
            adapter_bookshelf_book_name = itemView.findViewById(R.id.adapter_bookshelf_book_name);
            adapter_bookshelf_read_name = itemView.findViewById(R.id.adapter_bookshelf_read_name);
            adapter_bookshelf_continue = itemView.findViewById(R.id.adapter_bookshelf_continue);
            bookbrack_checkbox = itemView.findViewById(R.id.bookbrack_checkbox);
            delete = itemView.findViewById(R.id.delete);
        }
    }


    class FootViewHolder extends RecyclerView.ViewHolder {

        private View rootView;

        public FootViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private View rootView;

        TextView bookrack_read_time;
        TextView bookrack_coin_num;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            bookrack_read_time = itemView.findViewById(R.id.bookrack_read_time);
            bookrack_coin_num = itemView.findViewById(R.id.bookrack_coin_num);
        }
    }

    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_ITEM_TYPE) {
            return new HeaderViewHolder(getHeaderView());
        } else if (viewType == FOOTER_ITEM_TYPE) {
            return new FootViewHolder(getFooterView());
        } else if (viewType == AD_BOOK_SHELF_TYPE) {
            return ADViewHolderFactory.create(context, viewType);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bookshelf_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ItemViewHolder(view);
        }
    }

    public interface ShelfAdapterListener {
        void onBookReadContinue(Bookbrack Bookbrack);

        void onBookDelete(Bookbrack Bookbrack);

        void onBookClick(Bookbrack Bookbrack);

        void onBookLongClick(Bookbrack Bookbrack, View parent);

        void onFooterClick();
    }
}
