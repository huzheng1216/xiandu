package com.inveno.xiandu.view.main.my;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.AppInfoUtils;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des
 */
public class MineFragment extends Fragment {

    @BindView(R.id.iv_user_pic)
    ImageView pic;
    @BindView(R.id.tv_name)
    TextView user_name;
//    @BindView(R.id.tv_des)
//    TextView user_des;

    @OnClick(R.id.iv_user_pic)
    void pic() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
    }

    @OnClick(R.id.tv_name)
    void name() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
    }

//    @OnClick(R.id.tv_des)
//    void des() {
//        Toaster.showToastCenter(getContext(), "登陆");
//        ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
//    }

    //    @OnClick(R.id.bt_source)
//    void source() {
//        Toaster.showToastCenter(getContext(), "设置源");
//    }
//
//    @OnClick(R.id.bt_charts)
//    void charts() {
//        Toaster.showToastCenter(getContext(), "排行榜");
//    }
//
//    @OnClick(R.id.bt_setting)
//    void setting() {
//        Toaster.showToastCenter(getContext(), "设置");
//    }
    @OnClick(R.id.mine_send_luckymoney)
    void mine_send_luckymoney() {
        Toaster.showToastCenter(getContext(), "分红包");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_SEND_LUCKYMONEY).navigation();
    }

    @OnClick(R.id.mine_readed)
    void mine_readed() {
        Toaster.showToastCenter(getContext(), "阅读足迹");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_READ_FOOTPRINT).navigation();
    }

    @OnClick(R.id.mine_read)
    void mine_read() {
        Toaster.showToastCenter(getContext(), "阅读偏好");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_READ_PREFERENCES).navigation();
    }

    @OnClick(R.id.mine_qq)
    void mine_qq() {
        Toaster.showToastCenter(getContext(), "qq");
    }

    @OnClick(R.id.mine_setting)
    void mine_setting() {
//        Toaster.showToastCenter(getContext(), "设置");
        ARouter.getInstance().build(ARouterPath.ACTIVITY_SETTING).navigation();
    }

    private EventCanceler event_login;
    private EventCanceler event_logout;

    public static MineFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        setHeaderImage(R.drawable.ic_header_default);
        event_login = EventService.Companion.register(EventConstant.REFRESH_USER_DATA, new EventListener() {
            @Override
            public void onEvent(@NotNull String name, @NotNull String arg) {
                UserInfo userInfo = ServiceContext.userService().getUserInfo();
                if (TextUtils.isEmpty(userInfo.getUser_name())) {
                    user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
                } else {
                    user_name.setText(userInfo.getUser_name());
                }
            }
        });
        event_logout = EventService.Companion.register(EventConstant.LOGOUT, new EventListener() {
            @Override
            public void onEvent(@NotNull String name, @NotNull String arg) {
                user_name.setText("点我登陆");
                setHeaderImage(R.drawable.ic_header_default);//默认头像
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        tv = (TextView) view.findViewById(R.id.fragment_test_tv);

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String name = bundle.get("name").toString();
//            tv.setText(name);
//        }
        UserInfo userInfo = AppInfoUtils.getUserInfo();
        if (userInfo != null){
            if (TextUtils.isEmpty(userInfo.getUser_name())) {
                user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
            } else {
                user_name.setText(userInfo.getUser_name());
            }

            if (TextUtils.isEmpty(userInfo.getHead_url())){
                setHeaderImage(R.drawable.ic_header_default);//默认头像
            }
            else{
                setHeaderImage(userInfo.getHead_url());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        event_login.cancel();
        event_logout.cancel();
    }

    private void setHeaderImage(int defaul) {
//        Glide.with(this).load(R.drawable.ic_test_ad).centerCrop().placeholder(R.drawable.ic_header_default).into(pic);
        GlideUtils.LoadCircleImage(this.getContext(), R.drawable.ic_header_default, pic);
    }

    private void setHeaderImage(String url) {
//        Glide.with(this).load(R.drawable.ic_test_ad).centerCrop().placeholder(R.drawable.ic_header_default).into(pic);
        GlideUtils.LoadCircleImage(this.getContext(), R.drawable.ic_header_default, pic);
    }
}