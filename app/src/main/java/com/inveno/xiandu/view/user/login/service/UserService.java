package com.inveno.xiandu.view.user.login.service;

import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;
import com.inveno.xiandu.view.user.login.network.LoginAPI;

/**
 * @author yongji.wang
 * @date 2020/6/8 20:23
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UserService extends BaseSingleInstanceService {
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = JsonUtil.Companion.parseObject(AppPersistRepository.get().get(LoginAPI.USER_DATA_KEY), UserInfo.class);
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
