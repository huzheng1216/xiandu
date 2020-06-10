package com.inveno.xiandu.view.read;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.DividerItemDecorationStyleOne;
import com.inveno.xiandu.view.components.content.ContentControlView;
import com.inveno.xiandu.view.components.content.ZoomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created By huzheng
 * Date 2020-02-11
 * Des 目录页
 */
@Route(path = ARouterPath.ACTIVITY_CONTENT_MAIN)
public class ActivityReader extends BaseActivity {

    //数据
    @Autowired(name = "json")
    protected String json;
    @Autowired(name = "sectionUrl")
    protected String sectionUrl;
    @Autowired(name = "sectionName")
    protected String sectionName;
    private BookShelf book;
    private List<ChapterInfo> list = new ArrayList<>();
    //控件
    private ZoomRecyclerView recyclerView;
//    private TouchView touchView;
    private ContentControlView contentControlView;
    private ContentAdapter contentAdapter;

    //任务
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
//        setStatusBar(R.color.white, true);
        setContentView(R.layout.activity_book_content_main);

        recyclerView = findViewById(R.id.recyclerView);
//        touchView = findViewById(R.id.view_touch);
        contentControlView = findViewById(R.id.content_control);
//        touchView.setTouchListener(new TouchView.TouchListener() {
//            @Override
//            public void clickCenter() {
//                contentControlView.setVisibility(View.VISIBLE);
//                contentControlView.centerClick();
//            }
//
//            @Override
//            public void clickDouble() {
////                recyclerView.setEnableScale();
//            }
//        });
        contentAdapter = new ContentAdapter(this, list);
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecorationStyleOne(this, DividerItemDecorationStyleOne.VERTICAL_LIST, R.drawable.divider_recycleview, 0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setEnableScale(true);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setZoomListener(new ZoomRecyclerView.ZoomListener() {
            @Override
            public void onCenterClick() {
                contentControlView.setVisibility(View.VISIBLE);
                contentControlView.centerClick();
            }
        });
//        contentAdapter.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(BookContent bookContent) {
//            }
//        });
        initData();
    }

    private void initData() {
        book = GsonUtil.gsonToObject(json, BookShelf.class);

        if (book == null) {
            Toaster.showToastCenter(this, "无法获取书籍信息");
            return;
        }
//        if (book.getSource(this).getFetchType() == 1) {
//            showWebView();
//        } else {
//            getContentOneBy(sectionUrl);
//        }
    }


    @Override
    public void finish() {
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
        super.finish();
    }
}
