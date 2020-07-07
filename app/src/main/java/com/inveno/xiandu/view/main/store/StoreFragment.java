package com.inveno.xiandu.view.main.store;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.inveno.xiandu.R;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.tablayout.MyTabLayout;
import com.inveno.xiandu.view.main.MainActivity;
import com.inveno.xiandu.view.search.SearchActivityMain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des
 */
public class StoreFragment extends BaseFragment {

    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.MyTabLayout)
    MyTabLayout myTabLayout;
    @BindView(R.id.ViewPager)
    ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private String[] strings = new String[]{"推荐", "男频", "女频"};
    MyAdapter myAdapter;

    public boolean isVisible;

    public StoreFragment(){

    }
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
            StoreItemFragment storeItemFragment = new StoreItemFragment(s);
            fragments.add(storeItemFragment);
        }
        myAdapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(myAdapter);
        myTabLayout.setSelectedTabIndicatorWidth(DensityUtil.dip2px(getContext(), 10));
        myTabLayout.setSelectedTabIndicatorHeight(DensityUtil.dip2px(getContext(), 2));
        myTabLayout.setNeedSwitchAnimation(true);
        myTabLayout.setupWithViewPager(viewPager);
        ClickUtil.bindSingleClick(search, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivityMain.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), search, "photo").toBundle();
                startActivity(intent, bundle);
            }
        });

        return view;
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        super.onVisible(firstVisble);
        isVisible = true;
        report();
        if (firstVisble) {
            int gender = SPUtils.getInformain(Keys.READ_LIKE, 0);
            setDefaultItem(gender);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void setDefaultItem(int position) {
        //我这里mViewpager是viewpager子类的实例。如果你是viewpager的实例，也可以这么干。
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field = c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(viewPager, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myAdapter.notifyDataSetChanged();

        viewPager.setCurrentItem(position);
    }

    private void report(){
            for (int i = 0; i < fragments.size(); i++) {
                ((StoreItemFragment) fragments.get(i)).checkAndReport();
            }
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        isVisible = false;
    }
}