package com.inveno.xiandu.bean.book;

import com.inveno.xiandu.bean.BaseDataBean;

/**
 * @author yongji.wang
 * @date 2020/6/18 13:58
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RankingData extends BaseDataBean {

    private  long content_id;//小说id
    private String poster;//小说封面
    private String book_name;//小说名称
    private int rank_sort;//小说排名
    private String category_name;//分类名称

    public long getContent_id() {
        return content_id;
    }

    public void setContent_id(long content_id) {
        this.content_id = content_id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public int getRank_sort() {
        return rank_sort;
    }

    public void setRank_sort(int rank_sort) {
        this.rank_sort = rank_sort;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
