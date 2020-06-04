package com.inveno.xiandu.http.body;

/**
 * 处理统一返回数据格式的包装类
 * Created by zheng.hu on 2016/10/11.
 */
public class BaseRequest<T> {

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
