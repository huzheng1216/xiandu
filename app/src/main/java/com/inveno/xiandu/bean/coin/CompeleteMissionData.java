package com.inveno.xiandu.bean.coin;

/**
 * @author yongji.wang
 * @date 2020/6/20 18:56
 * @更新说明：
 * @更新时间：
 * @Version：
 */
public class CompeleteMissionData {
    private int code ;//是否正常完成标识：1.完成 0.未完成
    private int gold;//任务获取到的金币数
    private String message;//任务完成的信息
    private int signAllTimes;//总签到次数
    private int continueTimes;//连续签到次数


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSignAllTimes() {
        return signAllTimes;
    }

    public void setSignAllTimes(int signAllTimes) {
        this.signAllTimes = signAllTimes;
    }

    public int getContinueTimes() {
        return continueTimes;
    }

    public void setContinueTimes(int continueTimes) {
        this.continueTimes = continueTimes;
    }
}
