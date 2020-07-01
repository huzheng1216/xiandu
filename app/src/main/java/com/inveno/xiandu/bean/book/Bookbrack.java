package com.inveno.xiandu.bean.book;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * @author yongji.wang
 * @date 2020/6/17 19:36
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Entity
public class Bookbrack {
    @Id
    private Long content_id;//书本id

    private String book_name;//书名

    private String poster;//封面

    private int words_num;//阅读到的字数

    private String chapter_name;//阅读到的章节名称

    private int chapter_id;//阅读到的章节id

    private int page;//阅读到章节的第几页

    //书架创建时间
    private String time;

    @Transient
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Generated(hash = 1260541532)
    public Bookbrack() {
    }

    @Generated(hash = 1401419709)
    public Bookbrack(Long content_id, String book_name, String poster,
            int words_num, String chapter_name, int chapter_id, int page,
            String time) {
        this.content_id = content_id;
        this.book_name = book_name;
        this.poster = poster;
        this.words_num = words_num;
        this.chapter_name = chapter_name;
        this.chapter_id = chapter_id;
        this.page = page;
        this.time = time;
    }

    public Long getContent_id() {
        return this.content_id;
    }

    public void setContent_id(Long content_id) {
        this.content_id = content_id;
    }

    public String getBook_name() {
        return this.book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getPoster() {
        return this.poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getWords_num() {
        return this.words_num;
    }

    public void setWords_num(int words_num) {
        this.words_num = words_num;
    }

    public String getChapter_name() {
        return this.chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public int getChapter_id() {
        return this.chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
