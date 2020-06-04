package com.inveno.xiandu.view.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.view.BaseFragment;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 搜索界面
 */
public class SearchFragmentMain extends BaseFragment {

    private TextView search;

    public void SearchFragmentMain() {
    }

    @Override
    public ViewGroup initView() {
        ViewGroup inflate = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_main, null);
        search = inflate.findViewById(R.id.bt_search_fragment);

        ClickUtil.bindSingleClick(search, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_SEARCH).navigation();
            }
        });
        return inflate;
    }
}
