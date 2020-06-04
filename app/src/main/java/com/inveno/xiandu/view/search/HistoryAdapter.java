package com.inveno.xiandu.view.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;

import java.util.ArrayList;

/**
 * creat by: huzheng
 * date: 2019-10-18
 * description: 历史记录适配器
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<String> list;

    private OnHistoryClickListener onHistoryClickListener;

    public HistoryAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_search_main_history, viewGroup, false);
        return new HistoryViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int i) {
        viewHolder.textView.setText(list.get(i));
//        viewHolder.textView.setTextColor(z.getItemTextColor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setClickListener(OnHistoryClickListener onHistoryClickListener) {
        this.onHistoryClickListener = onHistoryClickListener;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.recycleview_search_history_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TextView textView = v.findViewById(R.id.recycleview_search_history_text);
            if (onHistoryClickListener != null)
                onHistoryClickListener.onClick(textView.getText().toString());
        }

        @Override
        public boolean onLongClick(View v) {
            TextView textView = v.findViewById(R.id.recycleview_search_history_text);
            if (onHistoryClickListener != null)
                onHistoryClickListener.onLongClick(textView.getText().toString());
            return true;
        }
    }

    public interface OnHistoryClickListener {
         void onClick(String title);
         void onLongClick(String title);
    }
}
