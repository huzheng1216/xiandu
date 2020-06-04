package com.inveno.xiandu.view.main.shelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.utils.ClickUtil;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/3/5
 * Des 书架适配
 */
public class ShelfAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Book> data;
    private ShelfAdapterListener shelfAdapterListener;

    public ShelfAdapter(Context context, List<Book> data) {
        this.context = context;
        this.data = data;
    }

    public void setShelfAdapterListener(ShelfAdapterListener shelfAdapterListener) {
        this.shelfAdapterListener = shelfAdapterListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (Const.ADAPTER_TYPE_FOOT == viewType) {
//            return onCreateFootViewHolder(parent, viewType);
//        } else {
            return onCreateDataViewHolder(parent, viewType);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof FootViewHolder) {
//            ClickUtil.bindSingleClick(holder.itemView, 500, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (shelfAdapterListener != null) {
//                        shelfAdapterListener.onAddClick();
//                    }
//                }
//            });
        } else {

            ItemViewHolder iholder = (ItemViewHolder) holder;
            iholder.bookName.setText(data.get(position).getName());
            Glide.with(context).load(data.get(position).getImg()).into(iholder.bookIc);
            ClickUtil.bindSingleClick(iholder.itemView, 500, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (shelfAdapterListener != null) {
                        shelfAdapterListener.onBookClick(position);
                    }
                }
            });
            iholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (shelfAdapterListener != null) {
                        shelfAdapterListener.onBookLongClick(position);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position == data.size() ? Const.ADAPTER_TYPE_FOOT : Const.ADAPTER_TYPE_DATA;
//    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView bookIc;
        TextView bookName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            bookIc = itemView.findViewById(R.id.adapter_bookshelf_book_img);
            bookName = itemView.findViewById(R.id.adapter_bookshelf_book_name);
        }
    }


    class FootViewHolder extends RecyclerView.ViewHolder {

        private View rootView;

        public FootViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
        }
    }

    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bookshelf, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ItemViewHolder(view);
    }

//    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bookshelf_foot, null);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(lp);
//        return new FootViewHolder(view);
//    }

    public interface ShelfAdapterListener{
//        void onAddClick();
        void onBookClick(int position);
        void onBookLongClick(int position);
    }
}
