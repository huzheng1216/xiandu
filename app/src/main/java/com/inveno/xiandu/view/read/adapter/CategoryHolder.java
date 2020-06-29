package com.inveno.xiandu.view.read.adapter;

import android.widget.TextView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.ChapterInfo;

/**
 * Created by zheng.hu on 17-5-16.
 */

public class CategoryHolder extends ViewHolderImpl<ChapterInfo> {

    private TextView mTvChapter;

    @Override
    public void initView() {
        mTvChapter = findById(R.id.category_tv_chapter);
    }

    @Override
    public void onBind(ChapterInfo value, int pos){
//        //首先判断是否该章已下载
//        Drawable drawable = null;
//
//        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
//        //如果没有链接地址表示是本地文件
//        if (value.getLink() == null){
//            drawable = ContextCompat.getDrawable(getContext(),R.drawable.selector_category_load);
//        }
//        else {
//            if (value.getBookId() != null
//                    && BookManager
//                    .isChapterCached(value.getBookId(),value.getTitle())){
//                drawable = ContextCompat.getDrawable(getContext(),R.drawable.selector_category_load);
//            }
//            else {
//                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
//            }
//        }

        mTvChapter.setSelected(false);
        mTvChapter.setTextColor(0xFF888888);
//        mTvChapter.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        mTvChapter.setText(value.getChapter_name());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_category;
    }

    public void setSelectedChapter(){
        mTvChapter.setTextColor(0xFF888888);
        mTvChapter.setSelected(true);
    }
}
