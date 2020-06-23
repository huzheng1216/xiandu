package com.inveno.xiandu.view.main.my;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.TitleBarBaseActivity;

/**
 * @author yongji.wang
 * @date 2020/6/9 17:13
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_READ_FOOTPRINT)
public class ReadFootprintActivity extends TitleBarBaseActivity {
    @Override
    public String getCenterText() {
        return "阅读足迹";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_read_footprint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }
}
