package com.inveno.xiandu.view.main.welfare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.coin.MissionData;
import com.inveno.xiandu.bean.coin.MissionDataList;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.MissionAdapter;
import com.inveno.xiandu.view.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

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

    @BindView(R.id.welfare_mission_recycle)
    RecyclerView welfare_mission_recycle;

    @BindView(R.id.welfare_coin_today)
    TextView welfare_coin_today;

    @BindView(R.id.welfare_coin_scroll)
    ScrollView welfare_coin_scroll;

    @BindView(R.id.no_mission_detail)
    TextView no_mission_detail;

    private MissionAdapter missionAdapter;
    private List<MissionData> missionDataList = new ArrayList<>();
    private UserCoin mUserCoin;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmen_welfare, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        welfare_mission_recycle.setLayoutManager(linearLayoutManager);

        missionAdapter = new MissionAdapter(getContext(), missionDataList);
        welfare_mission_recycle.setAdapter(missionAdapter);
        missionAdapter.setOnitemClickListener(new MissionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (ServiceContext.userService().isLogin()) {
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setCheckViewPager(1);
                    }
                } else {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
                }
            }
        });
        return view;
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        //第一次加载，获取金币
        if (ServiceContext.userService().isLogin()) {
            get_coin();
        } else {
            welfare_coin_today.setText(String.format("%s", "--"));
        }
        get_mission();
    }

    @OnClick(R.id.welfare_my_coin)
    public void jump_my_coin() {
        if (ServiceContext.userService().isLogin()) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_MY_COIN)
                    .withString("mUserCoin", GsonUtil.objectToJson(mUserCoin))
                    .navigation();
        } else {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
        }
    }

    //今日金币
    public void get_coin() {
        APIContext.coinApi().queryCoin()
                .onSuccess(new Function1<UserCoinOut, Unit>() {
                    @Override
                    public Unit invoke(UserCoinOut userCoin) {
                        mUserCoin = userCoin.getCoin();
                        welfare_coin_today.setText(String.format("%s", mUserCoin.getCurrent_coin()));
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
    }

    public void get_mission() {
        int[] typeIds = {4, 3};
        APIContext.coinApi().getMission(typeIds)
                .onSuccess(new Function1<MissionDataList, Unit>() {
                    @Override
                    public Unit invoke(MissionDataList missionDataList) {
                        if (missionDataList != null && missionDataList.getMission_list().size() > 0) {
                            missionAdapter.setsData(missionDataList.getMission_list());
                            no_mission_detail.setVisibility(View.GONE);
                        } else {
                            no_mission_detail.setVisibility(View.VISIBLE);
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        if (s.equals("404")) {
                            Toaster.showToastShort(getContext(), "获取任务失败:请求的数据不存在");
                        } else {
                            Toaster.showToastShort(getContext(), "获取任务失败:" + s);
                        }
                        no_mission_detail.setVisibility(View.VISIBLE);
                        return null;
                    }
                }).execute();
    }

}
