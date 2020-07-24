package com.inveno.xiandu.bean.store;

import com.inveno.xiandu.bean.book.Bookbrack;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/18 19:11
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class BannerDataList {
    private List<BannerDataBean> banner_list;

    public BannerDataList() {

    }

    public List<BannerDataBean> getBanner_list() {
        return banner_list;
    }

    public void setBanner_list(List<BannerDataBean> banner_list) {
        this.banner_list = banner_list;
    }
}
