package com.inveno.xiandu.view.adapter;

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
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ClassifyMenu;
import com.inveno.xiandu.utils.GlideUtils;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/12 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RelevantBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BookShelf> bookShelves;
    private OnItemClickListener mListener;

    public RelevantBookAdapter(Context context, List<BookShelf> bookShelves) {
        mContext = context;
        this.bookShelves = bookShelves;

    }

    public void setOnitemClickListener(OnItemClickListener onitemClickListener) {
        mListener = onitemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createDataViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReletantHolder) {
            ReletantHolder vholder = (ReletantHolder) holder;
            vholder.book_detail_bottom_bookname.setText(bookShelves.get(position).getBook_name());
            GlideUtils.LoadImage(mContext, bookShelves.get(position).getPoster(), vholder.book_detail_bottom_bookimg);
            vholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookShelves.size();
    }

    public static class ReletantHolder extends RecyclerView.ViewHolder {

        View lefMenuView;
        ImageView book_detail_bottom_bookimg;
        TextView book_detail_bottom_bookname;

        public ReletantHolder(View itemView) {
            super(itemView);
            this.lefMenuView = itemView;
            book_detail_bottom_bookimg = itemView.findViewById(R.id.book_detail_bottom_bookimg);
            book_detail_bottom_bookname = itemView.findViewById(R.id.book_detail_bottom_bookname);
        }
    }

    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_detail_more_book, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ReletantHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setsData(List<BookShelf> bookShelves) {
        this.bookShelves = bookShelves;
        notifyDataSetChanged();
    }
}
