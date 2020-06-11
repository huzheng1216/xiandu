package com.inveno.xiandu.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.invenohttp.api.LoginAPI;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.invenohttp.service.UserService;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;

/**
 * @author yongji.wang
 * @date 2020/6/10 16:10
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class AppInfoUtils {
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getAppVersion(Context context) {
        // 包管理器 可以获取清单文件信息
        PackageManager packageManager = context.getPackageManager();
        try {
            // 获取包信息
            // 参1 包名 参2 获取额外信息的flag 不需要的话 写0
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static UserInfo getUserInfo() {
        return ServiceContext.userService().getUserInfo();
    }
}
