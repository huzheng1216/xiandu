package com.inveno.xiandu.view.main.welfare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yongji.wang
 * @date 2020/6/9 11:02
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class WelfareFragment extends BaseFragment {

    @BindView(R.id.welfare_my_coin)
    TextView welfare_my_coin;

    @BindView(R.id.welfare_read_30_min_tv)
    TextView welfare_read_30_min_tv;

    @BindView(R.id.welfare_read_60_min_tv)
    TextView welfare_read_60_min_tv;

    @BindView(R.id.welfare_read_90_min_tv)
    TextView welfare_read_90_min_tv;

    @BindView(R.id.welfare_read_180_min_tv)
    TextView welfare_read_180_min_tv;

    @BindView(R.id.welfare_watch_video_tv)
    TextView welfare_watch_video_tv;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmen_welfare, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @OnClick(R.id.welfare_my_coin)
    void my_coin(){
        Toaster.showToastCenter(getContext(), "我的金币");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_MY_COIN).navigation();
    }

    @OnClick(R.id.welfare_read_30_min_tv)
    void read_30min(){

        Toaster.showToastCenter(getContext(), "阅读30分钟");
    }

    @OnClick(R.id.welfare_read_60_min_tv)
    void read_60min(){

        Toaster.showToastCenter(getContext(), "阅读60分钟");
    }

    @OnClick(R.id.welfare_read_90_min_tv)
    void read_90min(){

        Toaster.showToastCenter(getContext(), "阅读90分钟");
    }

    @OnClick(R.id.welfare_read_180_min_tv)
    void read_180min(){

        Toaster.showToastCenter(getContext(), "阅读180分钟");
    }

    @OnClick(R.id.welfare_watch_video_tv)
    void watch_video(){

        Toaster.showToastCenter(getContext(), "看广告");
    }
}
