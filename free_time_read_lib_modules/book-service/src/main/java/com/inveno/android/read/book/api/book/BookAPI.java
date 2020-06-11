package com.inveno.android.read.book.api.book;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.read.book.api.APIUrl;
import com.inveno.android.read.book.bean.Book;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.thread.ThreadUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BookAPI extends BaseSingleInstanceService {

    protected static final boolean MODULE_DEBUG = true;

    public StatefulCallBack<List<Book>> queryBooks(){
        if(MODULE_DEBUG){
            return new BaseStatefulCallBack<List<Book>>(){
                @Override
                public void execute() {
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            List<Book> bookList = new ArrayList<>();
                            bookList.add(new Book(1L,"人生中第一本小说"));
                            invokeSuccess(bookList);
                        }
                    });
                }
            };
        }else{
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<List<Book>>newCallBack(new TypeReference<List<Book>>(){}.getType())
                    .atUrl(APIUrl.getUrl("/book/list"))
                    .withArg(new LinkedHashMap<String, Object>())
                    .buildCallerCallBack();
        }
    }
}
