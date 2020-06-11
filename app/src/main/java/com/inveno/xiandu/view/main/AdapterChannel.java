package com.inveno.xiandu.view.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论适配器
 * Created by huzheng on 2017/8/31.
 */

public class AdapterChannel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FOOT = 0;
    public static final int TYPE_DATA = 1;

    private Context context;
    private List<BookShelf> data;
    private OnItemClickListener mListener;
    private boolean hasFoot = true;//是否展示加载更多

    public AdapterChannel(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_FOOT) {
//            return onCreateFootViewHolder(parent, viewType);
//        } else {
        return onCreateDataViewHolder(parent, viewType);
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder vholder = (ItemViewHolder) holder;
            vholder.name.setText(data.get(i).getBook_name());
            vholder.author.setText(data.get(i).getAuthor());
            vholder.catalog.setText(data.get(i).getCategory_name());
            vholder.words.setText(data.get(i).getWord_count() + "字");
            Glide.with(context).load(data.get(i).getPoster()).placeholder(R.mipmap.book_icon_default).into(vholder.pic);
        }
    }

    List<String> expands = new ArrayList<>();

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_DATA;
    }

    public void add(List<BookShelf> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;
        TextView author;
        TextView catalog;
        TextView words;
        ImageView pic;//举报

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.name);
            author = itemView.findViewById(R.id.author);
            catalog = itemView.findViewById(R.id.catalog);
            words = itemView.findViewById(R.id.words);
            pic = itemView.findViewById(R.id.pic);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView text;

        public FootViewHolder(View itemView) {
            super(itemView);
//            rootView = itemView.findViewById(R.id.category_foot_item_layout);
//            text = (TextView) itemView.findViewById(R.id.category_foot_item_title);
        }
    }

    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_channel, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ItemViewHolder(view);
    }

//    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_foot_progress1, null);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(lp);
//        return new FootViewHolder(view);
//    }


    public interface OnItemClickListener {
        void onItemClick(int position);

    }

}
