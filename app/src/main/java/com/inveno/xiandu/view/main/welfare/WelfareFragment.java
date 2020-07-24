package com.inveno.xiandu.view.main.welfare;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.donews.b.main.DoNewsAdNative;
import com.donews.b.main.info.DoNewsAD;
import com.donews.b.start.DoNewsAdManagerHolder;
import com.inveno.android.ad.contract.listener.RewardVideoAdListener;
import com.inveno.android.ad.contract.param.RewardAdParam;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.applocation.MainApplication;
import com.inveno.xiandu.bean.coin.CompeleteMissionData;
import com.inveno.xiandu.bean.coin.MissionData;
import com.inveno.xiandu.bean.coin.MissionDataList;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
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

    @BindView(R.id.welfare_title)
    TextView welfare_title;

    @BindView(R.id.welfare_today_coin_txt)
    TextView welfare_today_coin_txt;

    @BindView(R.id.welfare_coin_mission_txt)
    TextView welfare_coin_mission_txt;

    @BindView(R.id.video_loading)
    LinearLayout video_loading;

    private MissionAdapter missionAdapter;
    private List<MissionData> missionList = new ArrayList<>();
    private UserCoin mUserCoin;
    private int clickPosition = 0;
    private boolean videoVerify = false;

    private final RewardVideoAdListener rewardVideoAdListener = new RewardVideoAdListener() {
        @Override
        public void onAdShow() {
            LogUtils.H("onAdShow");
            videoVerify = false;
            if (video_loading.isShown()) {
                video_loading.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAdVideoBarClick() {
            LogUtils.H("onAdVideoBarClick");
        }

        @Override
        public void onAdClose() {
            LogUtils.H("onAdClose");
            if (videoVerify) {
                //观看有效，要提交任务完成
                APIContext.coinApi().completeMission(String.valueOf(missionList.get(clickPosition).getMission_id()))
                        .onSuccess(new Function1<CompeleteMissionData, Unit>() {
                            @Override
                            public Unit invoke(CompeleteMissionData compeleteMissionData) {
                                if (compeleteMissionData.getGold() > 0) {
                                    Toaster.showToastCenterShort(getContext(), String.format("+%s金币", compeleteMissionData.getGold()));
                                }
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
        }

        @Override
        public void onVideoComplete() {
            LogUtils.H("onVideoComplete");
        }

        @Override
        public void onRewardVerify(boolean var1) {
            LogUtils.H("onRewardVerify");
            videoVerify = var1;
        }

        @Override
        public void onSkippedVideo() {
            LogUtils.H("onSkippedVideo");
        }

        @Override
        public void onError(int var1, String var2) {
            LogUtils.H("onError");
        }
    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmen_welfare, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        welfare_mission_recycle.setLayoutManager(linearLayoutManager);

        missionAdapter = new MissionAdapter(getContext(), missionList);
        welfare_mission_recycle.setAdapter(missionAdapter);
        missionAdapter.setOnitemClickListener(new MissionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (ServiceContext.userService().isLogin()) {
                    if (getActivity() instanceof MainActivity) {
                        if (missionList.get(position).getMission_id() == 1) {
                            //签到要跳转到签到页
                            getActivity().startActivity(new Intent(getContext(), SignInActivity.class));

                        } else if (missionList.get(position).getMission_type() == 3) {
                            // TODO: 2020/7/21 跳转到看视频广告
                            //点击后显示转菊花加载
                            if (!video_loading.isShown()) {
                                video_loading.setVisibility(View.VISIBLE);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (video_loading.isShown()) {
                                        video_loading.setVisibility(View.GONE);
                                    }
                                }
                            }, 10 * 1000);
                            clickPosition = position;
                            requestVideoAd();
//                            testrequestAd();
                        } else {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.setCheckViewPager(1);
                        }
                    }
                } else {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
                }
            }
        });
        setTextTypeface();
        return view;
    }

    private void setTextTypeface() {

//        welfare_title.setTypeface(MainApplication.getInstance().getSanhansTypeface());
//        TextPaint tp = welfare_title.getPaint();
//        tp.setFakeBoldText(true);
//
//        welfare_today_coin_txt.setTypeface(MainApplication.getInstance().getSanhansTypeface());
//        TextPaint tpCoin = welfare_today_coin_txt.getPaint();
//        tpCoin.setFakeBoldText(true);
//
//        welfare_coin_mission_txt.setTypeface(MainApplication.getInstance().getSanhansTypeface());
//        TextPaint tpMission = welfare_coin_mission_txt.getPaint();
//        tpMission.setFakeBoldText(true);
    }

    private void requestVideoAd() {
        InvenoAdServiceHolder.getService().requestRewardAd(
                RewardAdParam.RewardAdParamBuilder.aRewardAdParam()
                        .withActivity(getActivity())
                        .withRewardVideoAdListener(rewardVideoAdListener).build()
        ).onSuccess(new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                LogUtils.H("requestVideoAd" + s);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer code, String msg) {
                if (video_loading.isShown()) {
                    video_loading.setVisibility(View.GONE);
                }
                Toaster.showToastCenterShort(getContext(), "小视频获取失败");
                LogUtils.H("requestVideoAd fail code:" + code + " msg:" + msg);
                return null;
            }
        }).execute();
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
        report();
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
        int[] typeIds = {1, 4, 3};
//        int[] typeIds = {4, 3};
        APIContext.coinApi().getMission(typeIds)
                .onSuccess(new Function1<MissionDataList, Unit>() {
                    @Override
                    public Unit invoke(MissionDataList missionDataList) {
                        if (missionDataList != null) {
                            missionList = missionDataList.getMission_list();
                        }
                        if (missionDataList != null && missionDataList.getMission_list().size() > 0) {
                            missionAdapter.setsData(missionList);
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
                            Toaster.showToastShort(getContext(), "获取任务失败");
                        }
//                        no_mission_detail.setVisibility(View.VISIBLE);
                        return null;
                    }
                }).execute();
    }

    private void report() {
        ReportManager.INSTANCE.reportPageImp(7, "", getContext(), ServiceContext.userService().getUserPid());
    }

}
