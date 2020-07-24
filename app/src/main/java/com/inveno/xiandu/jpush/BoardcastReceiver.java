package com.inveno.xiandu.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.jpush.android.service.PushService;

/**
 * @author yongji.wang
 * @date 2020/7/23
 * @更新说明：
 * @更新时间：2020/7/23
 * @Version：1.0.0
 */
public class BoardcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pushintent=new Intent(context, PushService.class);//启动极光推送的服务
        context.startService(pushintent);
    }
}
