package com.inveno.xiandu.http.service;

import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.bean.response.ResponseShelf;
import com.inveno.xiandu.http.body.BaseRequest;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 请求服务接口
 * Created by Administrator on 2016/9/23.
 */
public interface MRRequestService {

    @POST("behavior/book_shelf/list")
    Observable<BaseRequest<ResponseShelf>> getBookShelf(@Body Map<String, Object> map);

    @POST("behavior/book_shelf/add")
    Observable<BaseRequest<List<BookShelf>>> addBookShelf(@Body Map<String, Object> map);

    @POST("behavior/book_shelf/update")
    Observable<BaseRequest<List<BookShelf>>> updateBookShelf(@Body Map<String, Object> map);

    @FormUrlEncoded
    @POST("content/novel/info")
    Observable<BaseRequest<BookShelf>> getBookById(@Field("content_id") long content_id);

    @POST("content/chapter/info")
    Observable<BaseRequest<ChapterInfo>> getChapterInfo(@Body Map<String, Object> map);

    @POST("content/chapter/catalogue")
    Observable<BaseRequest<BookChapter>> getChapterList(@Body Map<String, Object> map);

    @POST("content/novel/recommend/list")
    Observable<BaseRequest<ResponseChannel>> getRecommendList(@Body Map<String, Object> map);
}
