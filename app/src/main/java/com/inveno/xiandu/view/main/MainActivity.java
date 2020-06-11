package com.inveno.xiandu.view.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.main.my.MineFragment;
import com.inveno.xiandu.view.main.shelf.BookShelfFragmentMain;
import com.inveno.xiandu.view.main.store.StoreFragment;
import com.inveno.xiandu.view.main.welfare.WelfareFragment;
import com.inveno.xiandu.view.search.SerchActivityMain;

import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterPath.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private MainViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private Toolbar toolbar;
    View searchLayout;
    ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestMyPermissions();
        setContentView(R.layout.activity_main);
        setStatusBar(R.color.white, true);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchLayout = findViewById(R.id.home_toolbar_search);
        pic = findViewById(R.id.home_tallbar_search_ic);
        ClickUtil.bindSingleClick(searchLayout, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SerchActivityMain.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pic, "photo").toBundle();
                startActivity(intent, bundle);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewpage_main);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > 1) {
                    toolbar.setVisibility(View.GONE);
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        List<Fragment> list = new ArrayList<>();
        list.add(new BookShelfFragmentMain());
        list.add(new StoreFragment());
        list.add(new WelfareFragment());
        list.add(MineFragment.newInstance("我的"));
        viewPagerAdapter.setList(list);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItem = item;
            switch (item.getItemId()) {
                case R.id.navigation_bookrack:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_bookstore:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_welfare:
//                    toolbar.setVisibility(View.GONE);
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_mine:
//                    toolbar.setVisibility(View.GONE);
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };
}
