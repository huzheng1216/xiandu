package com.inveno.xiandu.bean.user;

/**
 * @author yongji.wang
 * @date 2020/6/8 14:28
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UserInfo {
    private int pid;
//    性别,0-未知,1-男,2-女
    private String gender;
    private String user_name;
    private String head_url;
    private String phone_num;
    private long create_time;

    public UserInfo(){

    }
    public UserInfo(int pid, String gender, String user_name, String head_url) {
        this.pid = pid;
        this.gender = gender;
        this.user_name = user_name;
        this.head_url = head_url;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getHead_url() {
        return head_url;
    }

    public void setHead_url(String head_url) {
        this.head_url = head_url;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
}
