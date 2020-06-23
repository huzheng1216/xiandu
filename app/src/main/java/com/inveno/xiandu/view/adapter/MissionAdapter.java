package com.inveno.xiandu.view.adapter;

import android.content.Context;
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
import com.inveno.xiandu.bean.coin.MissionData;
import com.inveno.xiandu.utils.GlideUtils;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/12 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class MissionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mission_type = 0;
    private Context mContext;
    private List<MissionData> missionDatas;
    private OnItemClickListener mListener;

    public MissionAdapter(Context context, List<MissionData> missionDatas) {
        mContext = context;
        this.missionDatas = missionDatas;

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
        if (holder instanceof MissionHolder) {
            MissionHolder vholder = (MissionHolder) holder;
            MissionData missionData = missionDatas.get(position);
            if (missionData.getMax_times() > 1) {
                String nameStr = missionData.getMission_name() + "(%s/%s)";
                vholder.welfare_mission_name.setText(String.format(nameStr, missionData.getCompleted_times(), missionData.getMax_times()));
            } else {
                vholder.welfare_mission_name.setText(missionData.getMission_name());
            }
            vholder.welfare_coin_num.setText(String.format("+%s", missionData.getGold_num()));

            boolean isFinish = false;
            if (missionData.getMax_times() < 2) {
                if (missionData.getCompleted_times() <= 0) {
                    vholder.welfare_sign_in_tv.setText("去完成");
                    vholder.welfare_sign_in_tv.setBackground(mContext.getResources().getDrawable(R.drawable.blue_round_bg_15));
                    vholder.welfare_sign_in_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else {
                    isFinish = true;
                    vholder.welfare_sign_in_tv.setText("已完成");
                    vholder.welfare_sign_in_tv.setBackground(mContext.getResources().getDrawable(R.drawable.welfare_coin_finish_bg));
                    vholder.welfare_sign_in_tv.setTextColor(mContext.getResources().getColor(R.color.gray_9));
                }
            } else {
                if (missionData.getCompleted_times() < missionData.getMax_times()) {
                    vholder.welfare_sign_in_tv.setText("去完成");
                    vholder.welfare_sign_in_tv.setBackground(mContext.getResources().getDrawable(R.drawable.welfare_coin_video_ad_bg));
                    vholder.welfare_sign_in_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else {
                    isFinish = true;
                    vholder.welfare_sign_in_tv.setText("已完成");
                    vholder.welfare_sign_in_tv.setBackground(mContext.getResources().getDrawable(R.drawable.welfare_coin_finish_bg));
                    vholder.welfare_sign_in_tv.setTextColor(mContext.getResources().getColor(R.color.gray_9));
                }
            }
            boolean finalIsFinish = isFinish;
            vholder.welfare_sign_in_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!finalIsFinish) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return missionDatas.size();
    }

    public static class MissionHolder extends RecyclerView.ViewHolder {

        View lefMenuView;
        TextView welfare_mission_name;
        TextView welfare_coin_num;
        TextView welfare_sign_in_tv;

        public MissionHolder(View itemView) {
            super(itemView);
            this.lefMenuView = itemView;
            welfare_mission_name = itemView.findViewById(R.id.welfare_mission_name);
            welfare_coin_num = itemView.findViewById(R.id.welfare_coin_num);
            welfare_sign_in_tv = itemView.findViewById(R.id.welfare_sign_in_tv);
        }
    }

    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_welfare_mission, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MissionHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setsData(List<MissionData> missionDatas) {
        this.missionDatas = missionDatas;
        notifyDataSetChanged();
    }
}
