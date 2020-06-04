package com.inveno.xiandu.bean.book;


import android.content.Context;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * creat by: huzheng
 * date: 2019-08-09
 * description:漫画
 */
@Entity
public class Book {

    @Id(autoincrement = true)//设置自增长
    private Long id;

    //书名
    @NotNull
    private String name;

    //封面图片
    private String img;

    //作者
    private String author;

    //简介
    private String intro;

    //书籍类型
    private String category;

    //更新章节
    private String coll;

    //章节更新时间
    private String time;

    //书籍链接
    @NotNull
    private String url;

    //目录链接
    private String cattalogUrl;

    //源ID
    private long sourceId;

    //所属书架id
    private int shelfId;

    //所属书架
    private String hehe;

    //数据库更新时间
    @OrderBy
    private long updateTime;

    //目录列表
    @Transient
    private List<BookCatalog> bookCatalogs = new ArrayList<>();

    @Generated(hash = 1839243756)
    public Book() {
    }

    @Generated(hash = 1111324745)
    public Book(Long id, @NotNull String name, String img, String author,
                String intro, String category, String coll, String time,
                @NotNull String url, String cattalogUrl, long sourceId, int shelfId,
                String hehe, long updateTime) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.author = author;
        this.intro = intro;
        this.category = category;
        this.coll = coll;
        this.time = time;
        this.url = url;
        this.cattalogUrl = cattalogUrl;
        this.sourceId = sourceId;
        this.shelfId = shelfId;
        this.hehe = hehe;
        this.updateTime = updateTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getColl() {
        return coll;
    }

    public void setColl(String coll) {
        this.coll = coll;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCattalogUrl() {
        return this.cattalogUrl;
    }

    public void setCattalogUrl(String cattalogUrl) {
        this.cattalogUrl = cattalogUrl;
    }

    public long getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<BookCatalog> getBookCatalogs() {
        return bookCatalogs;
    }

    public void setBookCatalogs(List<BookCatalog> bookCatalogs) {
        this.bookCatalogs = bookCatalogs;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getHehe() {
        return this.hehe;
    }


    public void setHehe(String hehe) {
        this.hehe = hehe;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }



    public int getShelfId() {
        return this.shelfId;
    }



    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }
}
