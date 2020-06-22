package com.inveno.xiandu.bean.coin;

import com.inveno.xiandu.bean.BaseDataBean;

import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/6/19 15:04
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class CoinDetail extends BaseDataBean {
    private int coin_size;//明细数据的条数
    private int page;//本次访问的客户端传过来的第一个数据的索引位置
    private int pageSize;//每页的条数
    private List<CoinDetailData> coin_detail;//详情数据

    public CoinDetail() {

    }

    public int getCoin_size() {
        return coin_size;
    }

    public void setCoin_size(int coin_size) {
        this.coin_size = coin_size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<CoinDetailData> getCoin_detail() {
        return coin_detail;
    }

    public void setCoin_detail(List<CoinDetailData> coin_detail) {
        this.coin_detail = coin_detail;
    }
}
