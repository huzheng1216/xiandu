package com.inveno.xiandu.view.main.shelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.tablayout.MyTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 书架界面
 */
public class BookShelfFragmentMain extends BaseFragment {

    private MyTabLayout myTabLayout;
    private ViewPager viewPager;
//    private View editerBt;
//    private ImageView pic;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Fragment> fragments = new ArrayList<>();
    private List<BookShelf> bookShelves;
    private List<String> strings = new ArrayList<>();
    private MyAdapter myAdapter;

    public void SearchFragmentMain() {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bookShelves = SQL.getInstance(getContext()).getAllBookShelf();
        if (bookShelves.size() <= 0) {
            //没有书架,新建一个默认书架
            BookShelf bookShelf = new BookShelf();
            bookShelf.setId(0L);
            bookShelf.setName("书架");
            bookShelf.setTime(System.currentTimeMillis());
            long l = SQL.getInstance(getContext()).addBookShelf(bookShelf);
            bookShelf.setId(l);
            bookShelves.add(bookShelf);
        }
        //填充书架数据
        for (BookShelf bookShelf : bookShelves) {
            strings.add(bookShelf.getName());
            fragments.add(new ShelfFragment(bookShelf));
        }

        ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.fragment_bookrack_main, null);
        myTabLayout = inflate.findViewById(R.id.tablelayout_fragment_bookrack_main);
        viewPager = inflate.findViewById(R.id.viewpager_fragment_bookrack_main);
//        editerBt = inflate.findViewById(R.id.edit_search_main);
//        pic = inflate.findViewById(R.id.bt_search_main_back);
        swipeRefreshLayout = inflate.findViewById(R.id.SwipeRefreshLayout);
        myAdapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(myAdapter);
        myTabLayout.setSelectedTabIndicatorWidth(DensityUtil.dip2px(getContext(), 10));
        myTabLayout.setSelectedTabIndicatorHeight(DensityUtil.dip2px(getContext(), 2));
        myTabLayout.setNeedSwitchAnimation(true);
        myTabLayout.setupWithViewPager(viewPager);
//        ClickUtil.bindSingleClick(editerBt, 200, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), SerchActivityMain.class);
//                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) getActivity(), pic, "photo").toBundle();
//                startActivity(intent, bundle);
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toaster.showToastCenter(getContext(), "更新书架");
            }
        });

        return inflate;
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings.get(position);
        }
    }
}
