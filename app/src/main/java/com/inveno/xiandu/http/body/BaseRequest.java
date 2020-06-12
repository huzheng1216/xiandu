package com.inveno.xiandu.http.body;

/**
 * 处理统一返回数据格式的包装类
 * Created by zheng.hu on 2016/10/11.
 */
public class BaseRequest<T> {

    private int code;
    private String message;
    private String upack;//服务端下发的自定义用户相关信息，客户端无需解析，原样传回服务端上报体系，仅code200时下发
    private T data;

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

    public String getUpack() {
        return upack;
    }

    public void setUpack(String upack) {
        this.upack = upack;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
