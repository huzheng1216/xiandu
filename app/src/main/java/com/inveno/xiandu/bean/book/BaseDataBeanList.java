package com.inveno.xiandu.bean.book;

import com.inveno.xiandu.bean.BaseDataBean;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/15 20:42
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class BaseDataBeanList {
    private List<BaseDataBean> novel_list;

    public List<BaseDataBean> getNovel_list() {
        return novel_list;
    }

    public void setNovel_list(List<BaseDataBean> novel_list) {
        this.novel_list = novel_list;
    }
}
