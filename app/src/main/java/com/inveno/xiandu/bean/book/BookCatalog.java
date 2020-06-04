package com.inveno.xiandu.bean.book;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-12
 * Des 目录信息
 */
@Entity
public class BookCatalog {

    //自增ID
    @Id(autoincrement = true)
    private Long id;

    //书籍ID
    @NotNull
    private Long bookId;

    //章节地址
    private String url;

    //章节名称
    private String name;

    //本章节内容数
    private int count;

    //章节内容
    @Transient
    private List<BookContent> bookContents;

    @Generated(hash = 185693944)
    public BookCatalog() {
    }

    @Generated(hash = 1569211425)
    public BookCatalog(Long id, @NotNull Long bookId, String url, String name,
                       int count) {
        this.id = id;
        this.bookId = bookId;
        this.url = url;
        this.name = name;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookContent> getBookContents() {
        return bookContents;
    }

    public void setBookContents(List<BookContent> bookContents) {
        this.bookContents = bookContents;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getId() {
        return this.id;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
