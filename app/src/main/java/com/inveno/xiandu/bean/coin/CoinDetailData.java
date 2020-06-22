package com.inveno.xiandu.bean.coin;

import com.inveno.xiandu.bean.BaseDataBean;

/**
 * @author yongji.wang
 * @date 2020/6/19 15:04
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class CoinDetailData extends BaseDataBean {
    private String task_name;//事项名
    private long task_time;//事项金币操作时间
    private int coin;//金币数

    public CoinDetailData(){

    }
    public CoinDetailData(String task_name, long task_time, int coin) {
        this.task_name = task_name;
        this.task_time = task_time;
        this.coin = coin;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public long getTask_time() {
        return task_time;
    }

    public void setTask_time(long task_time) {
        this.task_time = task_time;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
