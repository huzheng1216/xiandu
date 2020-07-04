package com.inveno.xiandu.bean;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;

import static com.inveno.xiandu.bean.Result4List.type;

/**
 * Created by Administrator on 2017/4/27.
 */

public class Result<T> implements Serializable
{
    private static final long serialVersionUID = 5213230387175987834L;
    private int code;

    private String msg;

    private T data;

    private boolean success;

    public void setCode(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getMsg()
    {
        return this.msg;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public T getData()
    {
        return this.data;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public boolean getSuccess()
    {
        return this.success;
    }

    public static Result fromJson(String json, Class clazz)
    {
        Gson gson = new Gson();
        Type objectType = type(Result.class, clazz);
        return gson.fromJson(json, objectType);
    }
}
