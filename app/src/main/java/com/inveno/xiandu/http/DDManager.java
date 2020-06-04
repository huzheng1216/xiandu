package com.inveno.xiandu.http;

import android.content.Context;

import com.inveno.xiandu.http.biz.AccountBiz;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zheng.hu
 * @ClassName: DDManager
 * @Description: 网络请求综合管理类
 * @date 2016/9/26
 */
public class DDManager {

    private static DDManager mRManager;
    private AccountBiz accountBiz;

    private Context context;

    private DDManager(Context context) {
        this.context = context;
    }

    public synchronized static void init(Context context) {
        if (mRManager == null) {
            mRManager = new DDManager(context);
        }
    }

    public synchronized static DDManager getInstance() {
        return mRManager;
    }
}
