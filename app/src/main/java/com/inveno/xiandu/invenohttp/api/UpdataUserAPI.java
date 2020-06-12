package com.inveno.xiandu.invenohttp.api;

import android.text.TextUtils;

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
import com.inveno.xiandu.invenohttp.bacic_data.LoginUrl;
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
public class UpdataUserAPI extends BaseSingleInstanceService {

    public static String USER_DATA_KEY = "user_data_key";
    protected static final boolean MODULE_DEBUG = false;

    public StatefulCallBack<UserInfo> updataUser(LinkedHashMap<String, Object> updataParams) {
        StatefulCallBack<UserInfoList> realCallBack;
        if (MODULE_DEBUG) {
            realCallBack = new BaseStatefulCallBack<UserInfoList>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            List<UserInfo> userInfos = new ArrayList<>();
                            userInfos.add(new UserInfo(666, "1", "闲读小说", ""));
                            UserInfoList userInfoList = new UserInfoList();
                            userInfoList.setUser_list(userInfos);
                            invokeSuccess(userInfoList);
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> baseParam = ServiceContext.bacicParamService().getBaseParam();
            baseParam.putAll(updataParams);
            realCallBack = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<UserInfoList>newCallBack(new TypeReference<UserInfoList>() {
                    }.getType())
                    .atUrl(LoginUrl.getHttpUri(LoginUrl.UPDATA_INFO))
                    .withArg(baseParam)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<UserInfo> uiCallBack = new BaseStatefulCallBack<UserInfo>() {
            @Override
            public void execute() {
                realCallBack.execute();
            }
        };
        realCallBack.onSuccess(new Function1<UserInfoList, Unit>() {
            @Override
            public Unit invoke(UserInfoList userInfoList) {
                if (userInfoList.getUser_list().size() > 0) {
                    EventService.Companion.post(EventConstant.REFRESH_USER_DATA);
                    uiCallBack.invokeSuccess(userInfoList.getUser_list().get(0));
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
