package com.inveno.xiandu.bean.welfare;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/7/23
 * @更新说明：
 * @更新时间：2020/7/23
 * @Version：1.0.0
 */
public class RechangeBeanList {
    private int page;//页码
    private ArrayList<RechangeBean> recharge_list;//充值列表

    public RechangeBeanList() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<RechangeBean> getRecharge_list() {
        return recharge_list;
    }

    public void setRecharge_list(ArrayList<RechangeBean> recharge_list) {
        this.recharge_list = recharge_list;
    }
}
