package com.inveno.xiandu.view.main.store;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.tablayout.MyTabLayout;
import com.inveno.xiandu.view.search.SerchActivityMain;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.inveno.android.ad.config.ScenarioManifest.CATEGORY;
import static com.inveno.android.ad.config.ScenarioManifest.GUESS_YOU_LIKE;

/**
 * @author yongji.wang
 * @date 2020/6/12 16:43
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_CLASSIFY)
public class ClassifyActivity extends BaseActivity {

    private ImageView classify_search_img;

    private MyTabLayout myTabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private String[] strings = new String[]{"女生", "男生"};

    /**
     * 这个页面只有一个广告
     */
    private AdModel adModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.ranking_bg, true);
        setContentView(R.layout.activity_classify);
        ButterKnife.bind(this, this);

        initView();
    }

    protected void initView() {
        myTabLayout = findViewById(R.id.MyTabLayout);
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        viewPager = findViewById(R.id.ViewPager);

        classify_search_img = findViewById(R.id.classify_search_img);
        ClickUtil.bindSingleClick(classify_search_img, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this, SerchActivityMain.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(ClassifyActivity.this, classify_search_img, "photo").toBundle();
                startActivity(intent, bundle);
            }
        });

        for (String s : strings) {
            ClassifyItemFragment classifyItemFragment = new ClassifyItemFragment(s);
            fragments.add(classifyItemFragment);
        }
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        myTabLayout.setSelectedTabIndicatorWidth(DensityUtil.dip2px(this, 10));
        myTabLayout.setSelectedTabIndicatorHeight(DensityUtil.dip2px(this, 2));
        myTabLayout.setNeedSwitchAnimation(true);
        myTabLayout.setupWithViewPager(viewPager);

        loadAd();
    }

    public void click_back(View view) {
        finish();
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

    /**
     * 加载广告
     */
    private void loadAd(){
        InvenoAdServiceHolder.getService().requestInfoAd(CATEGORY, this).onSuccess(wrapper -> {
            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModel = new AdModel(wrapper);
            for (int i = 0; i < fragments.size(); i++) {
                ClassifyItemFragment classifyItemFragment = (ClassifyItemFragment) fragments.get(i);
                classifyItemFragment.notifyAdSetChanged(adModel);
            }
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

}
