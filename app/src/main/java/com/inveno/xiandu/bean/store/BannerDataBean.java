package com.inveno.xiandu.bean.store;

/**
 * @author yongji.wang
 * @date 2020/7/16
 * @更新说明：
 * @更新时间：2020/7/16
 * @Version：1.0.0
 */
public class BannerDataBean {

    private int banner_type;//类型 1书籍详情页 2 H5链接
    private String banner_img;//banner封面图
    private String banner_web_url;//H5链接
    private int banner_book_id;//小说ID
    private long start_time;//开始时间
    private long end_time;//结束时间

    public BannerDataBean(){

    }

    public BannerDataBean(int banner_type, String banner_img, String banner_web_url, int banner_book_id, long start_time, long end_time) {
        this.banner_type = banner_type;
        this.banner_img = banner_img;
        this.banner_web_url = banner_web_url;
        this.banner_book_id = banner_book_id;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public int getBanner_type() {
        return banner_type;
    }

    public void setBanner_type(int banner_type) {
        this.banner_type = banner_type;
    }

    public String getBanner_img() {
        return banner_img;
    }

    public void setBanner_img(String banner_img) {
        this.banner_img = banner_img;
    }

    public String getBanner_web_url() {
        return banner_web_url;
    }

    public void setBanner_web_url(String banner_web_url) {
        this.banner_web_url = banner_web_url;
    }

    public int getBanner_book_id() {
        return banner_book_id;
    }

    public void setBanner_book_id(int banner_book_id) {
        this.banner_book_id = banner_book_id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}
