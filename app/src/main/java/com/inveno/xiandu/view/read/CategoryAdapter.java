package com.inveno.xiandu.view.read;

import android.view.View;
import android.view.ViewGroup;

import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.view.read.adapter.CategoryHolder;
import com.inveno.xiandu.view.read.adapter.EasyAdapter;
import com.inveno.xiandu.view.read.adapter.IViewHolder;

/**
 * Created by newbiechen on 17-6-5.
 */

public class CategoryAdapter extends EasyAdapter<ChapterInfo> {
    private int currentSelected = 0;
    @Override
    protected IViewHolder<ChapterInfo> onCreateViewHolder(int viewType) {
        return new CategoryHolder();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CategoryHolder holder = (CategoryHolder) view.getTag();

        if (position == currentSelected){
            holder.setSelectedChapter();
        }

        return view;
    }

    public void setChapter(int pos){
        currentSelected = pos;
        notifyDataSetChanged();
    }
}
