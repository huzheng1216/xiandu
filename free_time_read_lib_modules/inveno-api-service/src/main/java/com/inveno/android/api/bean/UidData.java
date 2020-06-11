package com.inveno.android.api.bean;

public class UidData {
    private String code;
    private String server_time;
    private String uid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    //    code	YES	string	请求返回码，详见接口2.3接口返回代码
//    server_time	YES	string	服务端应答时时间戳，单位为秒。
//    uid	YES	string	返回的uid。
}
