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
 * Des 章节信息
 */
@Entity
public class ChapterInfo {

    //书籍ID
    private Long content_id;

    @Id
    private String chapter_id;

    private String book_name;

    //章节名称
    private String chapter_name;

    //章节顺序
    private int chapter_index;

    //本章节内容数
    private long word_count;

    //内容
    @Transient
    private String content;

    @Generated(hash = 1921544683)
    public ChapterInfo(Long content_id, String chapter_id, String book_name,
            String chapter_name, int chapter_index, long word_count) {
        this.content_id = content_id;
        this.chapter_id = chapter_id;
        this.book_name = book_name;
        this.chapter_name = chapter_name;
        this.chapter_index = chapter_index;
        this.word_count = word_count;
    }

    @Generated(hash = 1687309083)
    public ChapterInfo() {
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public long getWord_count() {
        return word_count;
    }

    public void setWord_count(long word_count) {
        this.word_count = word_count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getChapter_index() {
        return chapter_index;
    }

    public void setChapter_index(int chapter_index) {
        this.chapter_index = chapter_index;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public Long getContent_id() {
        return this.content_id;
    }

    public void setContent_id(Long content_id) {
        this.content_id = content_id;
    }
}
