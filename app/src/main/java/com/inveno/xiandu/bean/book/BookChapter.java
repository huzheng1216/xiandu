package com.inveno.xiandu.bean.book;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/6/10
 * Des 章节请求实体
 */
public class BookChapter {

    private int chapter_count;
    private int book_status;
    private List<ChapterInfo> chapter_list;

    public int getChapter_count() {
        return chapter_count;
    }

    public void setChapter_count(int chapter_count) {
        this.chapter_count = chapter_count;
    }

    public int getBook_status() {
        return book_status;
    }

    public void setBook_status(int book_status) {
        this.book_status = book_status;
    }

    public List<ChapterInfo> getChapter_list() {
        return chapter_list;
    }

    public void setChapter_list(List<ChapterInfo> chapter_list) {
        this.chapter_list = chapter_list;
    }
}
