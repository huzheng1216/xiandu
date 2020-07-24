package com.inveno.xiandu.bean.welfare;

import com.inveno.xiandu.bean.BaseDataBean;

/**
 * @author yongji.wang
 * @date 2020/7/23
 * @更新说明：
 * @更新时间：2020/7/23
 * @Version：1.0.0
 */
public class RechangeBean extends BaseDataBean {
    //    money	YES	int	充值金额
//    start_time	YES	timestamp	充值列表
//    recharge_state	YES	int	充值状态
//    coin	YES	int	充值金币
//    coin_state	YES	int	金币状态
    private int money;//充值金额
    private long start_time;//充值时间
    private int recharge_state;//充值状态  1待审核 2充值成功 3充值失败
    private int coin;//充值金币
    private int coin_state;//金币状态  //1未扣除 2已扣除  3已返还


    public RechangeBean() {
    }

    public RechangeBean(int money, long start_time, int recharge_state, int coin, int coin_state) {
        this.money = money;
        this.start_time = start_time;
        this.recharge_state = recharge_state;
        this.coin = coin;
        this.coin_state = coin_state;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public int getRecharge_state() {
        return recharge_state;
    }

    public void setRecharge_state(int recharge_state) {
        this.recharge_state = recharge_state;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getCoin_state() {
        return coin_state;
    }

    public void setCoin_state(int coin_state) {
        this.coin_state = coin_state;
    }
}
