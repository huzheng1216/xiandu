package com.inveno.xiandu.http.biz;

import android.content.Context;
import android.text.TextUtils;

import com.inveno.android.api.api.uid.UidParamsUtil;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.bean.response.ResponseShelf;
import com.inveno.xiandu.http.base.BaseBiz;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.view.read.setting.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public Observable<BaseRequest<ResponseShelf>> getBookShelf() {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("pid", ServiceContext.userService().getUserPid());
        return mMRRequestService.getBookShelf(param);
    }

    public Observable<BaseRequest<List<BookShelf>>> addBookShelf(long content_id, int status) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("content_id", content_id + "");
        param.put("status", status + "");
        return mMRRequestService.addBookShelf(param);
    }

    public Observable<BaseRequest<List<BookShelf>>> updateBookShelf(long content_id, int status) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("content_id", content_id + "");
        param.put("status", status + "");
        return mMRRequestService.updateBookShelf(param);
    }

    public Observable<BaseRequest<ResponseChannel>> getRecommendList(int channel_id, int num, int type) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("channel_id", channel_id + "");
        param.put("pid", ServiceContext.userService().getUserPid());
        param.put("num", num + "");
        param.put("type", type + "");
        return mMRRequestService.getRecommendList(param);
    }

    public Observable<BaseRequest<BookChapter>> getChapterList(String content_id, int page_num) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("pid", ServiceContext.userService().getUserPid());
        param.put("content_id", content_id);
        param.put("page_num", page_num + "");
        return mMRRequestService.getChapterList(param);
    }

    public Observable<BaseRequest<ChapterInfo>> getChapterInfo(String content_id, String chapter_id) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("pid", ServiceContext.userService().getUserPid());
        param.put("content_id", content_id);
        param.put("chapter_id", chapter_id);
        return mMRRequestService.getChapterInfo(param);
    }

    public Observable<BaseRequest> postReadTime(String id) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("content_id", id);
        param.put("pid", ServiceContext.userService().getUserPid());
        return mMRRequestService.postReadTime(param);
    }

    public Observable<BaseRequest> postReadProgress(String content_id, String chapter_id, int words_num) {
        LinkedHashMap<String,Object> param = new LinkedHashMap<>();
        UidParamsUtil.fillUidParams(param);
        String uid = InvenoServiceContext.uid().getUid();
        if(!TextUtils.isEmpty(uid)){
            param.put("uid", uid);
        }
        param.put("content_id", content_id);
        param.put("chapter_id", chapter_id);
        param.put("words_num", words_num);
        param.put("pid", ServiceContext.userService().getUserPid());
        return mMRRequestService.postReadTime(param);
    }
}
