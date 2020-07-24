package com.inveno.xiandu.bean.welfare;

/**
 * @author yongji.wang
 * @date 2020/7/21
 * @更新说明：
 * @更新时间：2020/7/21
 * @Version：1.0.0
 */
public class ActivityBean {
//    activity_name	YES	string	活动名称
//    activity_img	YES	sting	活动图标
//    activity_url	YES	string	H5链接
//    sign_in	YES	int	0表示不需要登录，1表示要登录
    private String activity_name;
    private String activity_img;
    private String activity_url;
    private int sign_in;

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getActivity_img() {
        return activity_img;
    }

    public void setActivity_img(String activity_img) {
        this.activity_img = activity_img;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public int getSign_in() {
        return sign_in;
    }

    public void setSign_in(int sign_in) {
        this.sign_in = sign_in;
    }
}
