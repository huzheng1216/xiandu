package com.inveno.xiandu.bean.book;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created By huzheng
 * Date 2020-02-12
 * Des 书籍内容
 */
@Entity
public class BookContent {

    //自增ID
    @Id(autoincrement = true)
    private Long id;

    //章节ID
    @NotNull
    private int catalogId;

    //内容地址
    private String url;

    //名称
    private String name;

    //下一页地址
    private String nextContenUrl;

    private boolean isDownload;

    @Generated(hash = 1559836836)
    public BookContent() {
    }

    @Generated(hash = 1530426351)
    public BookContent(Long id, int catalogId, String url, String name,
            String nextContenUrl, boolean isDownload) {
        this.id = id;
        this.catalogId = catalogId;
        this.url = url;
        this.name = name;
        this.nextContenUrl = nextContenUrl;
        this.isDownload = isDownload;
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

    public String getNextContenUrl() {
        return nextContenUrl;
    }

    public void setNextContenUrl(String nextContenUrl) {
        this.nextContenUrl = nextContenUrl;
    }

    public int getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public boolean getIsDownload() {
        return this.isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}
