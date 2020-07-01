package com.inveno.xiandu.invenohttp.api.book;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.BookbrackList;
import com.inveno.xiandu.bean.book.ReadTrack;
import com.inveno.xiandu.bean.book.ReadTrackList;
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
public class ReadTrackApi extends BaseSingleInstanceService {
    protected static final boolean MODULE_DEBUG = false;

    /**
     * 获取足迹内容
     * @param uid 唯一id
     * @param pid 用户id
     * @return
     */
    public StatefulCallBack<List<ReadTrack>> getReadTrackList(String uid, int pid) {

        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("uid", uid);
        mParams.put("pid", pid);
        mParams.putAll(bacicParams);

        StatefulCallBack<ReadTrackList> realCallback;

        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<ReadTrackList>newCallBack(new TypeReference<ReadTrackList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_READ_TRACK))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
        BaseStatefulCallBack<List<ReadTrack>> uiCallback = new BaseStatefulCallBack<List<ReadTrack>>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<ReadTrackList, Unit>() {
            @Override
            public Unit invoke(ReadTrackList readTrackList) {
                uiCallback.invokeSuccess(readTrackList.getRead_track());
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
     *
     * @param uid 用户uid
     * @param pid 用户Pid
     * @param content_id 书本id
     * @return
     */
    public StatefulCallBack<String> deleteReadTrack(String uid, int pid, long content_id) {

        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("uid", uid);
        mParams.put("pid", pid);
        mParams.put("content_id", content_id);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.DELETE_READ_TRACK))
                    .withArg(mParams)
                    .buildCallerCallBack();
        } else {
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.DELETE_READ_TRACK))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }
}
