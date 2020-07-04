package com.inveno.xiandu.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/27.
 */

public class Result4List<T> implements Serializable
{
    private static final long serialVersionUID = 5213230387175987834L;
    private int code;

    private String msg;

    private ArrayList<T> data;

    private boolean success;

    @SerializedName("has_more")
    private boolean hasMore;

    public boolean isHasMore()
    {
        return hasMore;
    }

    public void setHasMore(boolean hasMore)
    {
        this.hasMore = hasMore;
    }

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

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public boolean getSuccess()
    {
        return this.success;
    }

    public ArrayList<T> getData()
    {
        return data;
    }

    public void setData(ArrayList<T> data)
    {
        this.data = data;
    }

    public static Result4List fromJson(String json, Class clazz)
    {
        Gson gson = new Gson();
        Type objectType = type(Result4List.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public String toJson(Class<T> clazz)
    {
        Gson gson = new Gson();
        Type objectType = type(Result4List.class, clazz);
        return gson.toJson(this, objectType);
    }

    static ParameterizedType type(final Class raw, final Type... args)
    {
        return new ParameterizedType()
        {
            public Type getRawType()
            {
                return raw;
            }

            public Type[] getActualTypeArguments()
            {
                return args;
            }

            public Type getOwnerType()
            {
                return null;
            }
        };
    }
}
