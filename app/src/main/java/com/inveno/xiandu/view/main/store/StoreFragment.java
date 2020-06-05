package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.tablayout.MyTabLayout;
import com.inveno.xiandu.view.main.shelf.BookShelfFragmentMain;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des
 */
public class StoreFragment extends BaseFragment {

    @BindView(R.id.MyTabLayout)
    MyTabLayout myTabLayout;
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ViewPager)
    ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private String[] strings = new String[]{"推荐", "男频", "女频", "出版"};
    MyAdapter myAdapter;

    public static StoreFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putString("name", name);
        StoreFragment fragment = new StoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ViewGroup initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, view);

        for (String s : strings) {
            StoreItemFragment storeItemFragment = new StoreItemFragment();
            storeItemFragment.setTitle(s);
            fragments.add(storeItemFragment);
        }
        myAdapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(myAdapter);
        myTabLayout.setSelectedTabIndicatorWidth(DensityUtil.dip2px(getContext(), 10));
        myTabLayout.setSelectedTabIndicatorHeight(DensityUtil.dip2px(getContext(), 2));
        myTabLayout.setNeedSwitchAnimation(true);
        myTabLayout.setupWithViewPager(viewPager);
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


    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }
    }
}