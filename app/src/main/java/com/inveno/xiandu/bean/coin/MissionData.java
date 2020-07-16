package com.inveno.xiandu.bean.coin;

/**
 * @author yongji.wang
 * @date 2020/6/20 18:48
 * @更新说明：
 * @更新时间：
 * @Version：
 */
public class MissionData {
    private int mission_id;//任务ID
    private String mission_name;//任务名称
    private int gold_num;//任务可获得的金币数
    private int completed_times;//当天已完成次数
    private int max_times;//当天最大完成次数
    private int reading_time;//要求阅读时长：0为无要求
    private int continue_sign_in_num;//要求连续签到天数：0为无要求
    private int code;//1.满足条件；2.不满足次数3.不满足连续签到4.不满足阅读时长5.系统异常6.任务已完成
    private String message;//任务完成信息

    public MissionData() {

    }

    public int getMission_id() {
        return mission_id;
    }

    public void setMission_id(int mission_id) {
        this.mission_id = mission_id;
    }

    public String getMission_name() {
        return mission_name;
    }

    public void setMission_name(String mission_name) {
        this.mission_name = mission_name;
    }

    public int getGold_num() {
        return gold_num;
    }

    public void setGold_num(int gold_num) {
        this.gold_num = gold_num;
    }

    public int getCompleted_times() {
        return completed_times;
    }

    public void setCompleted_times(int completed_times) {
        this.completed_times = completed_times;
    }

    public int getMax_times() {
        return max_times;
    }

    public void setMax_times(int max_times) {
        this.max_times = max_times;
    }

    public int getReading_time() {
        return reading_time;
    }

    public void setReading_time(int reading_time) {
        this.reading_time = reading_time;
    }

    public int getContinue_sign_in_num() {
        return continue_sign_in_num;
    }

    public void setContinue_sign_in_num(int continue_sign_in_num) {
        this.continue_sign_in_num = continue_sign_in_num;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
