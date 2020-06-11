package com.inveno.xiandu.bean.book;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/3/18
 * Des 书架实体
 */
@Entity
public class BookShelf {

    //书籍ID
    @Id
    private Long content_id;

    //封面
    private String poster;

    //名称
    private String book_name;

    //小说作者
    private String author;


    //小说评分
    private float score;

    //小说人气
    private int popularity;

    //小说状态(0连载 1完本)
    private int book_status;

    //小说总字数
    private long word_count;

    //小说简介
    private String introduction;

    //分类名称
    private String category_name;

    @Transient
    private List<ChapterInfo> bookChapters;

    //书架创建时间
    private String time;

    @Generated(hash = 1921484685)
    public BookShelf(Long content_id, String poster, String book_name,
            String author, float score, int popularity, int book_status,
            long word_count, String introduction, String category_name,
            String time) {
        this.content_id = content_id;
        this.poster = poster;
        this.book_name = book_name;
        this.author = author;
        this.score = score;
        this.popularity = popularity;
        this.book_status = book_status;
        this.word_count = word_count;
        this.introduction = introduction;
        this.category_name = category_name;
        this.time = time;
    }

    @Generated(hash = 547688644)
    public BookShelf() {
    }

    public Long getContent_id() {
        return this.content_id;
    }

    public void setContent_id(Long content_id) {
        this.content_id = content_id;
    }

    public String getPoster() {
        return this.poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBook_name() {
        return this.book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getBook_status() {
        return this.book_status;
    }

    public void setBook_status(int book_status) {
        this.book_status = book_status;
    }

    public long getWord_count() {
        return this.word_count;
    }

    public void setWord_count(long word_count) {
        this.word_count = word_count;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategory_name() {
        return this.category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBookChapters(List<ChapterInfo> bookChapters) {
        if (this.bookChapters != null) {
            this.bookChapters.clear();
            this.bookChapters.addAll(bookChapters);
        } else {
            this.bookChapters = bookChapters;
        }
    }

    public List<ChapterInfo> getBookChapters() {
        return bookChapters;
    }
}
