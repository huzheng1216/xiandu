package com.inveno.xiandu.http.service;

import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.book.BookShelf;
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

    @FormUrlEncoded
    @POST("behavior/BookShelf/list")
    Observable<BaseRequest<List<BookShelf>>> getBookShelf(@Field("uid") String uid, @Field("pid") int pid);

    @FormUrlEncoded
    @POST("behavior/BookShelf/add")
    Observable<BaseRequest<List<BookShelf>>> addBookShelf(@Field("uid") String uid, @Field("pid") int pid, @Field("content_id") long content_id, @Field("status") int status);

    @FormUrlEncoded
    @POST("content/novel/info")
    Observable<BaseRequest<BookShelf>> getBookById(@Field("content_id") long content_id);

    @FormUrlEncoded
    @POST("content/chapter/info")
    Observable<BaseRequest<ChapterInfo>> getChapterInfo(@Field("content_id") long content_id, @Field("chapter_id") long chapter_id);

    @FormUrlEncoded
    @POST("content/chapter/catalogue")
    Observable<BaseRequest<BookChapter>> getChapterList(@Field("content_id") long content_id, @Field("page_num") int page_num);

    @POST("content/novel/recommend/list")
    Observable<BaseRequest<List<BookShelf>>> getRecommendList(@Body Map<String, String> map);
}
