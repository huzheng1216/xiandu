package com.inveno.xiandu.view.main;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inveno.android.device.param.provider.tools.NetWorkUtil;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.custom.MViewPager;
import com.inveno.xiandu.view.main.my.MineFragment;
import com.inveno.xiandu.view.main.shelf.BookShelfFragmentMain;
import com.inveno.xiandu.view.main.store.StoreFragment;
import com.inveno.xiandu.view.main.welfare.WelfareFragment;
import com.inveno.xiandu.view.search.SerchActivityMain;
import com.inveno.xiandu.view.splash.SplashActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterPath.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private MainViewPagerAdapter viewPagerAdapter;
    private MViewPager viewPager;
    private MenuItem menuItem;
    //    private Toolbar toolbar;
//    View searchLayout;
//    ImageView pic;
    private UserCoin userCoin;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestMyPermissions();
        setContentView(R.layout.activity_main);
        setStatusBar(R.color.white, true);
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        searchLayout = findViewById(R.id.home_toolbar_search);
//        pic = findViewById(R.id.home_tallbar_search_ic);
//        ClickUtil.bindSingleClick(searchLayout, 200, new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SerchActivityMain.class);
//                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pic, "photo").toBundle();
//                startActivity(intent, bundle);
//            }
//        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewpage_main);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position > 1) {
//                    toolbar.setVisibility(View.GONE);
//                }else{
//                    toolbar.setVisibility(View.VISIBLE);
//                }
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
        setDefaultItem(1);

        //是否第一次启动
        boolean firstLaunch = SPUtils.getInformain(Keys.AGREE_AGREEMENT, false);
        if (!firstLaunch) {
            //用户协议
            agreementDialog();
        }
    }

    private Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!NetWorkUtil.isNetworkAvailable(MainActivity.this)) {
                    //网络不可用
                    showErrorPopwindow(NETWORK_ERROR, new OnClickListener() {
                        @Override
                        public void onBackClick() {
                            if (NetWorkUtil.isNetworkAvailable(MainActivity.this)) {
                                ARouter.getInstance().build(ARouterPath.ACTIVITY_MAIN)
                                        .navigation();
                                finish();
                            }
                        }

                        @Override
                        public void onRefreshClick() {
                            if (NetWorkUtil.isNetworkAvailable(MainActivity.this)) {
                                ARouter.getInstance().build(ARouterPath.ACTIVITY_MAIN)
                                        .navigation();
                                finish();
                            }
                        }
                    });
                }
            }
        }, 2000);
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

    private long firstTime;// 记录点击返回时第一次的时间毫秒值

    //协议弹窗
    private void agreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_splash_agreement, null);
        TextView content = (TextView) v.findViewById(R.id.bt_splash_agreement_content);
        content.setText(getClickableSpan());
        //设置该句使文本的超连接起作用
        content.setMovementMethod(LinkMovementMethod.getInstance());
        View btn_sure = v.findViewById(R.id.bt_splash_agreement_ok);
        View btn_cancel = v.findViewById(R.id.bt_splash_agreement_cancel);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        ClickUtil.bindSingleClick(btn_sure, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.setInformain(Keys.AGREE_AGREEMENT, true);
                dialog.dismiss();
            }
        });
        ClickUtil.bindSingleClick(btn_cancel, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.showToastCenter(MainActivity.this, "在征得您的同意后我们才能为您提供服务");
            }
        });
    }

    //设置超链接文字
    private SpannableString getClickableSpan() {
        SpannableString spanStr = new SpannableString("欢迎使用闲读小说，为了更好地保护您的隐私和个人信息安全，我们根据国家相关法律规定拟定了《隐私政策》和《用户协议》，请您在使用前仔细阅读并确认您同意以上条款");
        //设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 43, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_AGREEMENT)
                        .withInt("setListener", 0)
                        .navigation();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Color.BLUE);
            }
        }, 43, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 43, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new BackgroundColorSpan(Color.WHITE), 43, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 50, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_AGREEMENT)
                        .withInt("setListener", 1)
                        .navigation();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Color.BLUE);
            }
        }, 50, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), 50, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new BackgroundColorSpan(Color.WHITE), 50, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 点击了返回按键
            exitApp(2000);// 退出应用
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //控制切换tab
    public void setCheckViewPager(int num) {
        viewPager.setCurrentItem(num);
    }

    //书架全选，需要隐藏底部tab
    public void setBottomVisiable() {

        if (bottomNavigationView.isShown()) {
            Animation animBottomOut = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_out);
            animBottomOut.setDuration(200);
            bottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.startAnimation(animBottomOut);

            //禁止左右滑动
            viewPager.setScrollable(false);
        } else {
            Animation animBottomIn = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_in);
            animBottomIn.setDuration(200);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.startAnimation(animBottomIn);

            //允许左右滑动
            viewPager.setScrollable(true);
        }
    }

    /**
     * 退出应用
     *
     * @param timeInterval 设置第二次点击退出的时间间隔
     */
    private void exitApp(long timeInterval) {
        if (System.currentTimeMillis() - firstTime >= timeInterval) {
            Toaster.showToast(this, "再按一次退出程序");
            firstTime = System.currentTimeMillis();
        } else {
            finish();// 销毁当前activity
            System.exit(0);// 完全退出应用
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

        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setCurrentItem(position);
    }
}
