package com.inveno.xiandu.bean.welfare;

/**
 * @author yongji.wang
 * @date 2020/7/17
 * @更新说明：
 * @更新时间：2020/7/17
 * @Version：1.0.0
 */
public class InvideBean {
    private String invite_code;

    public InvideBean() {
    }

    public InvideBean(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }
}
