package com.inveno.xiandu.invenohttp.api.user;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.event.EventService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.bean.user.UserInfoList;
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/8 16:14
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class GetUserAPI extends BaseSingleInstanceService {

    public static String USER_DATA_KEY = "user_data_key";
    protected static final boolean MODULE_DEBUG = false;
    private boolean isLogin = false;

    public StatefulCallBack<UserInfo> getUser() {
        LinkedHashMap<String, Object> baseParam = ServiceContext.bacicParamService().getBaseParam();
        if (baseParam.get("pid") != null) {
            isLogin = true;
        }
        StatefulCallBack<UserInfo> realCallBack;
        if (MODULE_DEBUG) {
            realCallBack = new BaseStatefulCallBack<UserInfo>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            UserInfo userInfo = new UserInfo(666, "1", "闲读小说", "");
                            invokeSuccess(userInfo);
                        }
                    });
                }
            };
        } else if (!isLogin) {
            realCallBack = new BaseStatefulCallBack<UserInfo>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            invokeSuccess(null);
                        }
                    });
                }
            };
        } else {
            realCallBack = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<UserInfo>newCallBack(new TypeReference<UserInfo>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_INFO))
                    .withArg(baseParam)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<UserInfo> uiCallBack = new BaseStatefulCallBack<UserInfo>() {
            @Override
            public void execute() {
                realCallBack.execute();
            }
        };
        realCallBack.onSuccess(new Function1<UserInfo, Unit>() {
            @Override
            public Unit invoke(UserInfo userInfo) {
                if (userInfo != null && userInfo.getPid() > 0) {
                    Log.i("wyjjjjjj", "getuser: " + JsonUtil.Companion.toJson(userInfo));
                    AppPersistRepository.get().save(USER_DATA_KEY, JsonUtil.Companion.toJson(userInfo));
                    ServiceContext.userService().setUserInfo(userInfo);
                    EventService.Companion.post(EventConstant.REFRESH_USER_DATA);

                    uiCallBack.invokeSuccess(userInfo);
                }
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallBack.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallBack;
    }
}
