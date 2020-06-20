package com.inveno.xiandu.bean.book;

/**
 * @author yongji.wang
 * @date 2020/6/17 13:40
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ClassifyMenu extends  Menu{

    private int category_id;
    private String category_name;

    public ClassifyMenu(){

    }

    public ClassifyMenu(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
