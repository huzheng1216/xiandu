package com.inveno.xiandu.bean.response;

import com.inveno.xiandu.bean.book.BookShelf;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/6/10
 * Des 书架返回
 */
public class ResponseShelf {

    private List<BookShelf> book_list;

    public List<BookShelf> getBook_list() {
        return book_list;
    }

    public void setBook_list(List<BookShelf> book_list) {
        this.book_list = book_list;
    }
}
