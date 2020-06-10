package com.inveno.xiandu.http.biz;

import android.content.Context;

import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.http.base.BaseBiz;
import com.inveno.xiandu.http.body.BaseRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;


/**
 * 处理账户相关业务
 * Created by 37399 on 2016/10/23.
 */
public class BookBiz extends BaseBiz {

    public BookBiz(Context context) {
        super(context);
    }

    public Observable<BaseRequest<BookShelf>> getBookById(long content_id) {
        return mMRRequestService.getBookById(content_id);
    }

    public Observable<BaseRequest<List<BookShelf>>> getBookShelf(String uid, int pid) {
        return mMRRequestService.getBookShelf(uid, pid);
    }

    public Observable<BaseRequest<List<BookShelf>>> addBookShelf(String uid, int pid, long content_id, int status) {
        return mMRRequestService.addBookShelf(uid, pid, content_id, status);
    }

    public Observable<BaseRequest<List<BookShelf>>> getRecommendList(int channel_id, int num, int type) {
        Map<String, String> map = new HashMap<String, String>(3);
        map.put("channel_id", channel_id + "");
        map.put("num", num + "");
        map.put("type", type + "");
        return mMRRequestService.getRecommendList(map);
    }

    public Observable<BaseRequest<BookChapter>> getChapterList(long content_id, int page_num) {
        return mMRRequestService.getChapterList(content_id, page_num);
    }

    public Observable<BaseRequest<ChapterInfo>> getChapterInfo(long content_id, long chapter_id) {
        return mMRRequestService.getChapterInfo(content_id, chapter_id);
    }
}
