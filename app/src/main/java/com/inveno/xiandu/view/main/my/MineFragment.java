package com.inveno.xiandu.view.main.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.basics.service.event.EventCanceler;
import com.inveno.android.basics.service.event.EventListener;
import com.inveno.android.basics.service.event.EventService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.AppInfoUtils;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.splash.ChoiseGenderActivity;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.iv_user_pic)
    ImageView pic;
    @BindView(R.id.tv_name)
    TextView user_name;
    //    @BindView(R.id.tv_des)
//    TextView user_des;
    @BindView(R.id.mine_read_gender_tv)
    TextView mine_read_gender_tv;

    @BindView(R.id.mine_my_coin)
    TextView mine_my_coin;

    @BindView(R.id.mine_today_coin)
    TextView mine_today_coin;

    @OnClick(R.id.iv_user_pic)
    void pic() {
        clickUser();
    }

    @OnClick(R.id.tv_name)
    void name() {
        clickUser();
    }

    private void clickUser() {
        if (isLogin) {
            Intent intent = new Intent(getActivity(), UserinfoActivity.class);
            startActivity(intent);
        } else {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
        }
    }

    @OnClick(R.id.mine_send_luckymoney)
    void mine_send_luckymoney() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_SEND_LUCKYMONEY).navigation();
    }

    @OnClick(R.id.mine_readed)
    void mine_readed() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_READ_FOOTPRINT).navigation();
    }

    @OnClick(R.id.mine_read)
    void mine_read() {
//        ARouter.getInstance().build(ARouterPath.ACTIVITY_READ_PREFERENCES).navigation();
        Intent intent = new Intent(getActivity(), ChoiseGenderActivity.class);
        intent.putExtra("request_code", ChoiseGenderActivity.MINE_REQUEST_CODE);
        startActivityForResult(intent, ChoiseGenderActivity.MINE_REQUEST_CODE);
    }

    @OnClick(R.id.mine_qq)
    void mine_qq() {
    }

    @OnClick(R.id.mine_setting)
    void mine_setting() {
//        Toaster.showToastCenter(getContext(), "设置");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_SETTING).navigation();
    }

    private EventCanceler event_login;
    private EventCanceler event_logout;
    private boolean isLogin = false;

    public static MineFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //今日金币
    public void get_coin() {
        APIContext.coinApi().queryCoin()
                .onSuccess(new Function1<UserCoinOut, Unit>() {
                    @Override
                    public Unit invoke(UserCoinOut userCoin) {
                        UserCoin mUserCoin = userCoin.getCoin();
                        mine_my_coin.setText(String.valueOf(mUserCoin.getBalance()));
                        mine_today_coin.setText(String.valueOf(mUserCoin.getCurrent_coin()));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        event_login.cancel();
        event_logout.cancel();
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        setHeaderImage(R.drawable.ic_header_default);
        event_login = EventService.Companion.register(EventConstant.REFRESH_USER_DATA, new EventListener() {
            @Override
            public void onEvent(@NotNull String name, @NotNull String arg) {
                isLogin = true;
                UserInfo userInfo = ServiceContext.userService().getUserInfo();
                if (TextUtils.isEmpty(userInfo.getUser_name())) {
                    user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
                } else {
                    user_name.setText(userInfo.getUser_name());
                }
                user_name.setClickable(false);
                user_name.setEnabled(false);
            }
        });
        event_logout = EventService.Companion.register(EventConstant.LOGOUT, new EventListener() {
            @Override
            public void onEvent(@NotNull String name, @NotNull String arg) {
                isLogin = false;
                user_name.setText("点我登陆");
                setHeaderImage(R.drawable.ic_header_default);//默认头像
            }
        });
        int gender = SPUtils.getInformain(Keys.READ_LIKE, 0);
        if (gender == 0) {
            mine_read_gender_tv.setText("");
        } else if (gender == 1) {
            mine_read_gender_tv.setText("男");
        } else if (gender == 2) {
            mine_read_gender_tv.setText("女");
        }
        return view;
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        if (firstVisble) {
            isLogin = false;
            UserInfo userInfo = ServiceContext.userService().getUserInfo();
            if (userInfo != null) {
                isLogin = true;
                if (TextUtils.isEmpty(userInfo.getUser_name())) {
                    user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
                } else {
                    user_name.setText(userInfo.getUser_name());
                }

                if (TextUtils.isEmpty(userInfo.getHead_url())) {
                    setHeaderImage(R.drawable.ic_header_default);//默认头像
                } else {
                    setHeaderImage(userInfo.getHead_url());
                }
            } else {
                APIContext.getUserAPI().getUser().onSuccess(new Function1<UserInfo, Unit>() {
                    @Override
                    public Unit invoke(UserInfo userInfo) {
                        if (TextUtils.isEmpty(userInfo.getUser_name())) {
                            user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
                        } else {
                            user_name.setText(userInfo.getUser_name());
                        }
                        if (TextUtils.isEmpty(userInfo.getHead_url())) {
                            setHeaderImage(R.drawable.ic_header_default);//默认头像
                        } else {
                            setHeaderImage(userInfo.getHead_url());
                        }
                        return null;
                    }
                }).onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
            }
        }
        get_coin();
    }

    private void setHeaderImage(int defaul) {
//        Glide.with(this).load(R.drawable.ic_test_ad).centerCrop().placeholder(R.drawable.ic_header_default).into(pic);
        GlideUtils.LoadCircleImage(this.getContext(), R.drawable.ic_header_default, pic);
    }

    private void setHeaderImage(String url) {
//        Glide.with(this).load(R.drawable.ic_test_ad).centerCrop().placeholder(R.drawable.ic_header_default).into(pic);
        GlideUtils.LoadCircleImage(this.getContext(), R.drawable.ic_header_default, pic);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChoiseGenderActivity.MINE_REQUEST_CODE && resultCode == ChoiseGenderActivity.MINE_REQUEST_CODE) {
            int gender = 0;
            if (data != null) {
                gender = data.getIntExtra("gender", 0);

                if (gender == 0) {
                    mine_read_gender_tv.setText("");
                } else if (gender == 1) {
                    mine_read_gender_tv.setText("男");
                } else if (gender == 2) {
                    mine_read_gender_tv.setText("女");
                }
            }
        }
    }
}