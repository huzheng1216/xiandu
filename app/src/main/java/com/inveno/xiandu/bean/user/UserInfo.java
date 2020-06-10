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
    private String gender;
    private String user_name;
    private String head_url;

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
}
