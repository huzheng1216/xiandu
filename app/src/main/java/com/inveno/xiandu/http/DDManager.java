package com.inveno.xiandu.http;

import android.content.Context;
import android.text.TextUtils;

import com.inveno.android.api.api.uid.UidParamsUtil;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.bean.response.ResponseShelf;
import com.inveno.xiandu.http.biz.AccountBiz;
import com.inveno.xiandu.http.biz.BookBiz;
import com.inveno.xiandu.http.body.BaseRequest;

import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author zheng.hu
 * @ClassName: DDManager
 * @Description: 网络请求综合管理类
 * @date 2016/9/26
 */
public class DDManager {

    private static DDManager mRManager;
    private AccountBiz accountBiz;
    private BookBiz bookBiz;

    private Context context;

    private DDManager(Context context) {
        this.context = context;
        bookBiz = new BookBiz(context);
    }

    public synchronized static void init(Context context) {
        if (mRManager == null) {
            mRManager = new DDManager(context);
        }
    }

    public synchronized static DDManager getInstance() {
        return mRManager;
    }


    /**
     * 获取书架
     *
     * @return
     */
    public Observable<BaseRequest<ResponseShelf>> getBookShelf() {
        return bookBiz.getBookShelf();
    }

    /**
     * 添加书架
     *
     * @param content_id
     * @param status
     * @return
     */
    public Observable<BaseRequest<List<BookShelf>>> addBookShelf(long content_id, int status) {
        return bookBiz.addBookShelf(content_id, status);
    }

    /**
     * 移除书架
     *
     * @param content_id
     * @param status
     * @return
     */
    public Observable<BaseRequest<List<BookShelf>>> updateBookShelf(long content_id, int status) {
        return bookBiz.updateBookShelf(content_id, status);
    }

    /**
     * 根据ID获取书籍
     *
     * @param content_id
     * @return
     */
    public Observable<BaseRequest<BookShelf>> getBookById(long content_id) {
        return bookBiz.getBookById(content_id);
    }

    /**
     * 获取频道数据
     * @param channel_id
     * @param num
     * @param type
     * @return
     */
    public Observable<BaseRequest<ResponseChannel>> getRecommendList(int channel_id, int num, int type) {
        return bookBiz.getRecommendList(channel_id, num, type);
    }


    /**
     * 获取章节列表
     * @param content_id
     * @param page_num
     * @return
     */
    public Observable<BaseRequest<BookChapter>> getChapterList(String content_id, int page_num) {
        return bookBiz.getChapterList(content_id, page_num);
    }


    /**
     * 获取章节信息
     * @param content_id
     * @param chapter_id
     * @return
     */
    public Observable<BaseRequest<ChapterInfo>> getChapterInfo(String content_id, String chapter_id) {
        return bookBiz.getChapterInfo(content_id, chapter_id);
    }


}
