package com.inveno.xiandu.view.main.my;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.TitleBarBaseActivity;

/**
 * @author yongji.wang
 * @date 2020/6/9 17:12
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_READ_PREFERENCES)
public class ReadPreferencesActivity extends TitleBarBaseActivity {
    @Override
    public String getCenterText() {
        return "阅读偏好";
    }

    @Override
    public int layoutID() {
        return 0;
    }
}
