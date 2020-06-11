package com.inveno.xiandu.view.read;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.view.components.MyImageView;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-10
 * Des
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.SearchViewHolder> {

    private List<ChapterInfo> data;
    private Context context;

    public ContentAdapter(Context context, List<ChapterInfo> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_book_content, null, false);
        return new SearchViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder viewHolder, final int i) {
//        Glide.with(context).load(data.get(i).getUrl())
//                .placeholder(R.drawable.ic_loading_64x64)
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        viewHolder.pic.setImageDrawable(resource);
//                    }
//                });
//        Glide.with(context).load(data.get(i).get())
//                .placeholder(R.drawable.ic_book_icon_default)
//                .into(viewHolder.pic);
        viewHolder.num.setText((i + 1) + "");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private MyImageView pic;
        private TextView num;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            pic = itemView.findViewById(R.id.content_adapter_pic);
            num = itemView.findViewById(R.id.content_adapter_num);
        }
    }

    interface OnItemClickListener {
        void onClick(ChapterInfo bookContent);
    }

}
