package com.inveno.xiandu.invenohttp.api.book;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.BookbrackList;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/18 19:04
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class BookbrackApi extends BaseSingleInstanceService {
    protected static final boolean MODULE_DEBUG = false;

    /**
     * 获取书架内容
     * @param uid 唯一id
     * @param pid 用户id
     * @return
     */
    public StatefulCallBack<List<Bookbrack>> getBookbrackList(String uid, int pid) {

        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("uid", uid);
        mParams.put("pid", pid);
        mParams.putAll(bacicParams);

        StatefulCallBack<BookbrackList> realCallback;

        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<BookbrackList>newCallBack(new TypeReference<BookbrackList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BOOKBRACK_DATA_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
        BaseStatefulCallBack<List<Bookbrack>> uiCallback = new BaseStatefulCallBack<List<Bookbrack>>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<BookbrackList, Unit>() {
            @Override
            public Unit invoke(BookbrackList bookbrackList) {
                uiCallback.invokeSuccess(bookbrackList.getBook_list());
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        }).execute();

        return uiCallback;
    }

    /**
     * 添加书本
     * @param uid 唯一id
     * @param pid 用户id
     * @param content_id 书籍
     * @return
     */
    public StatefulCallBack<String> addBookbrack(String uid, int pid, long content_id) {

        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("uid", uid);
        mParams.put("pid", pid);
        mParams.put("content_id", content_id);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BOOKBRACK_ADD))
                    .withArg(mParams)
                    .buildCallerCallBack();
        } else {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BOOKBRACK_ADD))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }

    /**
     *
     * @param uid 用户uid
     * @param pid 用户Pid
     * @param content_ids 书本id的数组
     * @param status	状态：1：添加，0：删除
     * @return
     */
    public StatefulCallBack<String> updataBookbrack(String uid, int pid, ArrayList<Long> content_ids, int status) {

        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("uid", uid);
        mParams.put("pid", pid);
        mParams.put("content_ids", content_ids);
        mParams.put("status", status);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BOOKBRACK_UPDATA))
                    .withArg(mParams)
                    .buildCallerCallBack();
        } else {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BOOKBRACK_UPDATA))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }
}
