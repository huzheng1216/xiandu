package com.inveno.xiandu.http;

import android.content.Context;

import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.http.biz.AccountBiz;
import com.inveno.xiandu.http.biz.BookBiz;
import com.inveno.xiandu.http.body.BaseRequest;

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
     * @param uid
     * @param pid
     * @return
     */
    public Observable<BaseRequest<List<BookShelf>>> getBookShelf(String uid, int pid) {
        return bookBiz.getBookShelf(uid, pid);
    }

    /**
     * 添加书架
     *
     * @param uid
     * @param pid
     * @return
     */
    public Observable<BaseRequest<List<BookShelf>>> addBookShelf(String uid, int pid, long content_id, int status) {
        return bookBiz.addBookShelf(uid, pid, content_id, status);
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
    public Observable<BaseRequest<List<BookShelf>>> getRecommendList(int channel_id, int num, int type) {
        return bookBiz.getRecommendList(channel_id, num, type);
    }


}
