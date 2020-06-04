package com.inveno.xiandu.bean.book;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created By huzheng
 * Date 2020/3/18
 * Des 自定义书架
 */
@Entity
public class BookShelf {

    //书架ID
    @Id(autoincrement = true)//设置自增长
    private Long id;

    //书架名称
    private String name;

    //书架创建时间
    private long time;

    @Generated(hash = 1505875902)
    public BookShelf(Long id, String name, long time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    @Generated(hash = 547688644)
    public BookShelf() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
