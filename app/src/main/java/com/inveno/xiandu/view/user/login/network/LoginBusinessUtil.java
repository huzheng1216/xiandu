package com.inveno.xiandu.view.user.login.network;

/**
 * @author yongji.wang
 * @date 2020/6/8 15:42
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class LoginBusinessUtil {
    private static String HOST_URI = "http://192.168.1.23:9000";//测试环境

    public static String GET_CODE = "/user/get_code";
    public static String LOGIN_PHONE = "/user/login_phone";
    public static String UPDATA_INFO = "/user/update_info";
    public static String GET_INFO = "/user/get_info";

    public static String getHttpUri(String url){
        return HOST_URI + url;
    }
}
