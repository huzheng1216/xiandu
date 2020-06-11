package com.inveno.xiandu.view.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.ChapterInfo;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-10
 * Des
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.SearchViewHolder> {

    private List<ChapterInfo> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CatalogAdapter(Context context, List<ChapterInfo> data) {
        this.data = data;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_book_catalog, null, false);
        return new SearchViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder viewHolder, final int i) {
        viewHolder.name.setText(data.get(i).getChapter_name());
        if (onItemClickListener != null) {
//            ClickUtil.bindSingleClick(viewHolder.itemView, 500, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onClick(data.get(i));
//                }
//            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(data.get(i));
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
        private TextView name;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.catalog_adapter_name);
        }
    }

    interface OnItemClickListener {
        void onClick(ChapterInfo bookCatalog);
    }

}
