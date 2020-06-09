package com.inveno.xiandu.view.user.login.network;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.event.EventService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;
import com.inveno.xiandu.view.user.login.event.EventConstant;
import com.inveno.xiandu.view.user.login.service.ServiceContext;

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
public class LoginAPI extends BaseSingleInstanceService {

    public static String USER_DATA_KEY = "user_data_key";
    protected static final boolean MODULE_DEBUG = true;

    public StatefulCallBack<UserInfo> login(String phoneNum, String verificationCode) {
        StatefulCallBack<List<UserInfo>> realCallBack;
        if (MODULE_DEBUG) {
            realCallBack = new BaseStatefulCallBack<List<UserInfo>>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            List<UserInfo> userInfos = new ArrayList<>();
                            userInfos.add(new UserInfo(666, "1", "闲读小说", ""));
                            invokeSuccess(userInfos);
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> getCodeData = new LinkedHashMap<>();
            getCodeData.put("phone_num", phoneNum);
            getCodeData.put("verification_code", verificationCode);
            realCallBack = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<List<UserInfo>>newCallBack(new TypeReference<List<UserInfo>>() {
                    }.getType())
                    .atUrl(LoginUrl.getHttpUri(LoginUrl.GET_CODE))
                    .withArg(getCodeData)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<UserInfo> uiCallBack = new BaseStatefulCallBack<UserInfo>() {
            @Override
            public void execute() {
                realCallBack.execute();
            }
        };
        realCallBack.onSuccess(new Function1<List<UserInfo>, Unit>() {
            @Override
            public Unit invoke(List<UserInfo> userInfos) {
                if (userInfos.size() > 0) {
                    UserInfo userInfo = userInfos.get(0);
                    AppPersistRepository.get().save(USER_DATA_KEY, JsonUtil.Companion.toJson(userInfo));
                    ServiceContext.userService().setUserInfo(userInfo);
                    EventService.Companion.post(EventConstant.LOGIN_SUCCESS);
                    uiCallBack.invokeSuccess(userInfos.get(0));
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
