package com.inveno.xiandu.bean.book;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-12
 * Des
 */
public class BookDetail {

    private String intro;//简介
    private List<BookCatalog> bookCatalogs;//目录列表

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<BookCatalog> getBookCatalogs() {
        return bookCatalogs;
    }

    public void setBookCatalogs(List<BookCatalog> bookCatalogs) {
        this.bookCatalogs = bookCatalogs;
    }
}
