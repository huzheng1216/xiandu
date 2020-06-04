package com.inveno.xiandu.http.callback;

/**
 * Describe:
 * Created by ${zheng.hu} on 2016/9/26.
 */
public abstract class RequestCallBack<T> {

    //开始
    public void onStart() {};

    //进度
    public void onProgress(int progress) {};

    //成功
    public abstract void onSuccess(T t);

    //失败
    public abstract void onFailure(String msg);
}
