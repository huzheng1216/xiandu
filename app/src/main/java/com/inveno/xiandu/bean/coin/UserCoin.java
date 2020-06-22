package com.inveno.xiandu.bean.coin;

import com.inveno.xiandu.bean.BaseDataBean;

/**
 * @author yongji.wang
 * @date 2020/6/19 13:47
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UserCoin extends BaseDataBean {
    private int balance;//金币余额
    private int current_coin;//当日总金币
    private int total_coin;//累计总金币
    private int total_hongbao_coin;//红包累计总金币
    private String exchage_rate;//金币汇率
    public UserCoin() {

    }

    public UserCoin(int balance, int current_coin, int total_coin, int total_hongbao_coin) {
        this.balance = balance;
        this.current_coin = current_coin;
        this.total_coin = total_coin;
        this.total_hongbao_coin = total_hongbao_coin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCurrent_coin() {
        return current_coin;
    }

    public void setCurrent_coin(int current_coin) {
        this.current_coin = current_coin;
    }

    public int getTotal_coin() {
        return total_coin;
    }

    public void setTotal_coin(int total_coin) {
        this.total_coin = total_coin;
    }

    public int getTotal_hongbao_coin() {
        return total_hongbao_coin;
    }

    public void setTotal_hongbao_coin(int total_hongbao_coin) {
        this.total_hongbao_coin = total_hongbao_coin;
    }

    public String getExchage_rate() {
        return exchage_rate;
    }

    public void setExchage_rate(String exchage_rate) {
        this.exchage_rate = exchage_rate;
    }
}
