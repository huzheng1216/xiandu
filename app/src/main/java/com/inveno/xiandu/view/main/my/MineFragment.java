package com.inveno.xiandu.view.main.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.Toaster;

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
    TextView name;
    @BindView(R.id.tv_des)
    TextView des;

    @OnClick(R.id.iv_user_pic)
    void pic() {
        Toaster.showToastCenter(getContext(), "登陆");
    }

    @OnClick(R.id.tv_name)
    void name() {
        Toaster.showToastCenter(getContext(), "登陆");
    }

    @OnClick(R.id.tv_des)
    void des() {
        Toaster.showToastCenter(getContext(), "登陆");
    }

    @OnClick(R.id.bt_source)
    void source() {
        Toaster.showToastCenter(getContext(), "设置源");
    }

    @OnClick(R.id.bt_charts)
    void charts() {
        Toaster.showToastCenter(getContext(), "排行榜");
    }

    @OnClick(R.id.bt_setting)
    void setting() {
        Toaster.showToastCenter(getContext(), "设置");
    }

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

    }
}