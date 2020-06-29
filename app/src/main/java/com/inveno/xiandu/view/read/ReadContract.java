package com.inveno.xiandu.view.read;

import com.inveno.xiandu.bean.book.ChapterInfo;

import java.util.List;

/**
 * Created by zheng.hu on 17-5-16.
 */

public interface ReadContract extends BaseContract {
    interface View extends BaseView, com.inveno.xiandu.view.read.adapter.BaseContract.BaseView {
        void showCategory(List<ChapterInfo> bookChapterList);

        void finishChapter();

        void errorChapter();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void loadCategory(String bookId);

        void loadChapter(String bookId, List<ChapterInfo> bookChapterList);
    }
}
