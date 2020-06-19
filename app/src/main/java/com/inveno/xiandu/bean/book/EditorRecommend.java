package com.inveno.xiandu.bean.book;

import com.inveno.xiandu.bean.BaseDataBean;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/15 17:44
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class EditorRecommend extends BaseDataBean {

    private long content_id;
    private ArrayList<String> list_images;
    private String book_name;
    private String title;
    private int display_type;

    public EditorRecommend() {
    }

    public long getContent_id() {
        return content_id;
    }

    public void setContent_id(long content_id) {
        this.content_id = content_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public ArrayList<String> getList_images() {
        return list_images;
    }

    public void setList_images(ArrayList<String> list_images) {
        this.list_images = list_images;
    }

    public String getBook_namen() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDisplay_type() {
        return display_type;
    }

    public void setDisplay_type(int display_type) {
        this.display_type = display_type;
    }
}
