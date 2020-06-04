package com.inveno.xiandu.view.book;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.bean.book.BookCatalog;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.PullRecyclerViewGroup2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created By huzheng
 * Date 2020-02-11
 * Des 目录页
 */
@Route(path = ARouterPath.ACTIVITY_CATALOG_MAIN)
public class DetailActivity extends BaseActivity {

    //数据
    @Autowired(name = "json")
    protected String json;
    private Book book;
    private List<BookCatalog> list = new ArrayList<>();
    private CatalogAdapter catalogAdapter;
    private Disposable disposable;//检索任务

    //控件
    @BindView(R.id.header_bar_back_tv)
    TextView title;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.catalog_main_booc_ic)
    ImageView pic;
    @BindView(R.id.catalog_main_booc_name)
    TextView bookName;
    @BindView(R.id.catalog_main_booc_author)
    TextView bookAuthor;
    @BindView(R.id.catalog_main_booc_category)
    TextView bookCategory;
    @BindView(R.id.catalog_main_booc_intro)
    TextView bookIntro;
    @BindView(R.id.AppBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.NestedScrollView)
    PullRecyclerViewGroup2 pullRecyclerViewGroup2;
    @BindView(R.id.progress_bar_read)
    ProgressBar progressBar;
    @BindView(R.id.bt_coll)
    TextView collBt;
    @BindView(R.id.bt_read)
    View readBt;
    @BindView(R.id.bt_refresh)
    View refreshBt;
    //返回
    @OnClick(R.id.header_bar_back_img)
    void onClick() {
        ActivityCompat.finishAfterTransition(this);
    }
    //刷新
    @OnClick(R.id.bt_refresh)
    void refresh() {
        refreshBt.setVisibility(View.GONE);
        readBt.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getCatalog();
    }
    //收藏
    @OnClick(R.id.bt_coll)
    void coll() {
        boolean b = SQL.getInstance(this).insertBook(book);
        if (b) {
            Toaster.showToastCenter(this, "已添加到书架");
            enableCollBt();
        }
    }
    //倒序排列
    @OnClick(R.id.sort)
    void sort() {
        Collections.reverse(list);
        catalogAdapter.notifyDataSetChanged();
    }
    //立即阅读
    @OnClick(R.id.bt_read)
    void read() {
        if (list.size() > 0) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                    .withString("json", GsonUtil.objectToJson(book))
                    .withString("sectionUrl", list.get(0).getUrl())
                    .withString("sectionName", list.get(0).getName())
                    .navigation();
        } else {
            Toaster.showToastCenter(this, "没有章节");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_catalog_main2);
        ButterKnife.bind(this);
        setStatusBar(R.color.white, true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int distans = appBarLayout.getHeight() - DensityUtil.dip2px(DetailActivity.this, 50);
                if (distans + verticalOffset <= 0) {
                    title.setVisibility(View.VISIBLE);
                } else {
                    title.setVisibility(View.GONE);
                }
                if (verticalOffset == 0) {
                    pullRecyclerViewGroup2.isTopVisible = true;
                } else {
                    pullRecyclerViewGroup2.isTopVisible = false;
                }
            }
        });

        catalogAdapter = new CatalogAdapter(this, list);
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(catalogAdapter);
        catalogAdapter.setOnItemClickListener(new CatalogAdapter.OnItemClickListener() {
            @Override
            public void onClick(BookCatalog bookCatalog) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                        .withString("json", GsonUtil.objectToJson(book))
                        .withString("sectionUrl", bookCatalog.getUrl())
                        .withString("sectionName", bookCatalog.getName())
                        .navigation();
            }
        });
        initData();
    }

    private void initData() {
        book = GsonUtil.gsonToObject(json, Book.class);

        if (book == null) {
            Toaster.showToast(this, "无法获取书籍信息");
            return;
        }
        Glide.with(this).load(book.getImg()).into(pic);
        title.setText(book.getName());
        bookName.setText(book.getName());
        bookAuthor.setText(book.getAuthor());
        bookCategory.setText(book.getCategory());
        if (SQL.getInstance(this).queryBookByName(book.getName()).size() > 0) {
            enableCollBt();
        }
        getCatalog();
    }

    //解析内容
    private void getCatalog() {
//        SearchTool.getInstance().getCatalog(book.getUrl(), book.getSource(this))
//                .subscribe(new Observer<BookDetail>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(BookDetail bookDetail) {
//                        book.setIntro(bookDetail.getIntro());
//                        book.setBookCatalogs(bookDetail.getBookCatalogs());
//                        bookIntro.setText(bookDetail.getIntro());
//                        list.addAll(bookDetail.getBookCatalogs());
//                        catalogAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        progressBar.setVisibility(View.GONE);
//                        readBt.setVisibility(View.GONE);
//                        refreshBt.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        if (list.size() > 0) {
//                            progressBar.setVisibility(View.GONE);
//                            readBt.setVisibility(View.VISIBLE);
//                            refreshBt.setVisibility(View.GONE);
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            readBt.setVisibility(View.GONE);
//                            refreshBt.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
    }

    //已收藏
    private void enableCollBt() {
        collBt.setEnabled(false);
        collBt.setTextColor(0xff888888);
        collBt.setText("已收藏");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
