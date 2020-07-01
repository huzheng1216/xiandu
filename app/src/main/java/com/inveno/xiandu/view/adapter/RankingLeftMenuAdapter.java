package com.inveno.xiandu.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.ClassifyMenu;
import com.inveno.xiandu.bean.book.RankingMenu;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/12 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RankingLeftMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RankingMenu> mMenus;
    private int lastChoise = 0;
    private OnItemClickListener mListener;

    public RankingLeftMenuAdapter(Context context, List<RankingMenu> menus) {
        mContext = context;
        mMenus = menus;

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
        if (holder instanceof LeftMenusHolder) {
            LeftMenusHolder vholder = (LeftMenusHolder) holder;
            vholder.left_menu_name.setText(mMenus.get(position).getRanking_name());
            if (position == lastChoise) {
                vholder.left_menu_name.setBackground(mContext.getResources().getDrawable(R.drawable.left_menu_select_bg));
                vholder.left_menu_name.setTextColor(mContext.getResources().getColor(R.color.clr_normal));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);//4个参数按顺序分别是左bai上右下
                vholder.left_menu_name.setPadding(0, 56, 0, 56);
                vholder.left_menu_name.setLayoutParams(layoutParams);
            } else {
                vholder.left_menu_name.setBackgroundColor(Color.parseColor("#F6F7F9"));
                vholder.left_menu_name.setTextColor(mContext.getResources().getColor(R.color.gray_3));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 50, 0);//4个参数按顺序分别是左bai上右下
                vholder.left_menu_name.setPadding(0, 56, 0, 56);
                vholder.left_menu_name.setLayoutParams(layoutParams);
            }
            vholder.left_menu_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(lastChoise);
                    notifyItemChanged(position);
                    lastChoise = position;
                    mListener.onItemClick(position);
                }
            });
        }
    }

    public void setSelectMenu(int position){
        notifyItemChanged(lastChoise);
        notifyItemChanged(position);
        lastChoise = position;
        mListener.onItemClick(position);
    }
    @Override
    public int getItemCount() {
        return mMenus.size();
    }

    public static class LeftMenusHolder extends RecyclerView.ViewHolder {

        View lefMenuView;
        TextView left_menu_name;

        public LeftMenusHolder(View itemView) {
            super(itemView);
            this.lefMenuView = itemView;
            left_menu_name = itemView.findViewById(R.id.left_menu_name);
        }
    }

    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_left_menu_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new LeftMenusHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setMenusData(List<RankingMenu> menus){
        mMenus = menus;
        notifyDataSetChanged();
    }
}
