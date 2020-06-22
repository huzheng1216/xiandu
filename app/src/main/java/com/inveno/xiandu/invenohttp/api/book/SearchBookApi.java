package com.inveno.xiandu.invenohttp.api.book;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/20 16:41
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class SearchBookApi extends BaseSingleInstanceService {
    protected static final boolean MODULE_DEBUG = false;

    /**
     * 获取推荐小说,根据组合值获取指定内容
     *
     * @return
     */
    public StatefulCallBack<BookShelfList> searchBook(String content, int page_num) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("content", content);
        mParams.put("page_num", page_num);
        mParams.putAll(bacicParams);

        StatefulCallBack<BookShelfList> realCallback;
        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<BookShelfList>newCallBack(new TypeReference<BookShelfList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.SEARCH_BOOK))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<BookShelfList> uiCallback = new BaseStatefulCallBack<BookShelfList>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<BookShelfList, Unit>() {
            @Override
            public Unit invoke(BookShelfList bookShelfList) {
                List<BookShelf> novel_list = new ArrayList<>();
                for (BookShelf editorRecommend : bookShelfList.getNovel_list()) {
                    editorRecommend.setType(RecyclerBaseAdapter.DEFAUL_RECOMMEND);
                    novel_list.add(editorRecommend);
                }
                BookShelfList mList = new BookShelfList();
                mList.setNovel_list(novel_list);
                uiCallback.invokeSuccess(mList);
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallback;
    }
}
