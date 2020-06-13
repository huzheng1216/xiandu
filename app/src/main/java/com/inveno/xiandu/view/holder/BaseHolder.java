package com.inveno.xiandu.view.holder;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author yongji.wang
 * @date 2020/6/13 17:32
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder {

    public BaseHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setData(Context context, T object);
}
