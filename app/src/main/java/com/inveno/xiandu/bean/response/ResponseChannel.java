package com.inveno.xiandu.bean.response;

import com.inveno.xiandu.bean.book.BookShelf;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/6/10
 * Des
 */
public class ResponseChannel {

    private List<BookShelf> novel_list;

    public List<BookShelf> getNovel_list() {
        return novel_list;
    }

    public void setNovel_list(List<BookShelf> novel_list) {
        this.novel_list = novel_list;
    }
}
