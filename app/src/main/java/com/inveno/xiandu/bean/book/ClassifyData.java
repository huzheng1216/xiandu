package com.inveno.xiandu.bean.book;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/17 13:52
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ClassifyData {
    private int category_id;
    private int novel_count;
    private List<BookShelf> novel_list;

    public ClassifyData() {
    }
    public ClassifyData(int novel_count, List<BookShelf> novel_list) {
        this.novel_count = novel_count;
        this.novel_list = novel_list;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getNovel_count() {
        return novel_count;
    }

    public void setNovel_count(int novel_count) {
        this.novel_count = novel_count;
    }

    public List<BookShelf> getNovel_list() {
        return novel_list;
    }

    public void setNovel_list(List<BookShelf> novel_list) {
        this.novel_list = novel_list;
    }
}
