package com.inveno.xiandu.view.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.main.my.MineFragment;
import com.inveno.xiandu.view.main.shelf.BookRackFragmentMain;
import com.inveno.xiandu.view.main.store.StoreFragment;

import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterPath.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private MainViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestMyPermissions();
        setContentView(R.layout.activity_main);
        setStatusBar(R.color.white, true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewpage_main);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
//        list.add(new SearchFragmentMain());
        list.add(new BookRackFragmentMain());
        list.add(new StoreFragment());
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
                case R.id.navigation_mine:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
}
