package com.inveno.xiandu.view.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.bean.BaseDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/13 13:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public abstract class RecyclerBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int EMPTY_ITEM_TYPE = -1;

    //0-10 公共使用
    public static final int HEADER_ITEM_TYPE = 3;
    public static final int FOOTER_ITEM_TYPE = 1;
    public static final int CONTENT_ITEM_TYPE = 2;

    //11-100 其他类型数据使用
    public static final int CENTER_TITLE = 11;//中间间隔标题
    public static final int BIG_IMAGE = 12;//大图
    public static final int MORE_IMAGE = 13;//多图
    public static final int SMALL_IMAGE = 14;//小图
    public static final int NOT_IMAGE = 15;//无图
    public static final int PEOPLE_RECOMMEND = 16;//男女生精选
    public static final int DEFAUL_RECOMMEND = 0;//人气精选
    //101-200广告使用
    public static final int AD_ITEM_TYPE_1 = 101;
    public static final int AD_ITEM_TYPE_2 = 102;
    public static final int AD_ITEM_TYPE_3 = 103;
    public static final int AD_ITEM_TYPE_4 = 104;

    protected View headerView;
    protected View footerView;

    protected List<BaseDataBean> mDataList = new ArrayList<>();

    protected abstract View getHeaderView();

    protected abstract View getFooterView();

    @Override
    public int getItemCount() {
        if (mDataList.size() == 0) {
            return 0;
        }
        if (getHeaderView() != null && getFooterView() != null) {
            return mDataList.size() + 2;
        } else if (getHeaderView() != null || getFooterView() != null) {
            return mDataList.size() + 1;
        } else {
            return mDataList.size();
        }
    }
}
