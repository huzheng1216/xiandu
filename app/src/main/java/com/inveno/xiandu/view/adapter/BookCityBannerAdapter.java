package com.inveno.xiandu.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.store.BannerDataBean;
import com.inveno.xiandu.utils.GlideUtils;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/7/14
 * @更新说明：
 * @更新时间：2020/7/14
 * @Version：1.0.0
 */
public class BookCityBannerAdapter extends BannerAdapter<BannerDataBean, BookCityBannerAdapter.BannerViewHolder> {

    private List<BannerDataBean> mDatas;
    private Context mContext;
    private OnBannerClickListener clickListener;

    public BookCityBannerAdapter(Context context, List<BannerDataBean> datas) {
        super(datas);
        mDatas = datas;
        mContext = context;
    }

    public void setBannerClickListener(OnBannerClickListener bannerClickListener) {
        clickListener = bannerClickListener;
    }

    public void setmDatas(List<BannerDataBean> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setBackground(mContext.getResources().getDrawable(R.drawable.white_round_bg_15));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerDataBean data, int position, int size) {
        GlideUtils.LoadImage(mContext, mDatas.get(position).getBanner_img(), holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position);
            }
        });
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView view) {
            super(view);
            this.imageView = view;
        }
    }

    public interface OnBannerClickListener {
        void onItemClick(int position);
    }
}
