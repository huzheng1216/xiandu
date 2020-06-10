package com.inveno.xiandu.view.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.StringTools;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-10
 * Des
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {

    private List<BookShelf> data;
    private Context context;
    private String searchKey;
    private OnItemClickListener onItemClickListener;

    public SearchResultAdapter(Context context, List<BookShelf> data, String searchKey) {
        this.data = data;
        this.context = context;
        this.searchKey = searchKey;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_search_result, null, false);
        return new SearchViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder viewHolder, final int i) {

        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(DensityUtil.dip2px(context,5));
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options= RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(context).load(data.get(i).getPoster()).apply(options).into(viewHolder.pic);
        viewHolder.title.setText(StringTools.highlight(context, data.get(i).getBook_name(), searchKey, "#FF55A772"));
        viewHolder.author.setText(data.get(i).getAuthor());
        viewHolder.coll.setText(data.get(i).getCategory_name());
//        viewHolder.source.setText(data.get(i).getSource(context).getName());
        if (onItemClickListener != null) {
            ClickUtil.bindSingleClick(viewHolder.itemView, 500, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(data.get(i), viewHolder.pic);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView pic;
        private TextView title;
        private TextView author;
        private TextView coll;
        private TextView source;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            pic = itemView.findViewById(R.id.search_adapter_pic);
            title = itemView.findViewById(R.id.search_adapter_title);
            author = itemView.findViewById(R.id.search_adapter_author);
            coll = itemView.findViewById(R.id.search_adapter_coll);
            source = itemView.findViewById(R.id.search_adapter_source);
        }
    }

    interface OnItemClickListener {
        void onClick(BookShelf book, ImageView pic);
    }

}
