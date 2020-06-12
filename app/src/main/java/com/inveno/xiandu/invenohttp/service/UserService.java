package com.inveno.xiandu.invenohttp.service;

import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;
import com.inveno.xiandu.invenohttp.api.LoginAPI;

/**
 * @author yongji.wang
 * @date 2020/6/8 20:23
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UserService extends BaseSingleInstanceService {
    private UserInfo userInfo;

    @Override
    protected void onCreate() {
        super.onCreate();
        userInfo = getUserInfo();
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = JsonUtil.Companion.parseObject(AppPersistRepository.get().get(LoginAPI.USER_DATA_KEY), UserInfo.class);
        }
        return userInfo;
    }

    public int getUserPid(){
        if(userInfo==null){
            return 0;
        }else{
            return userInfo.getPid();
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
