package com.inveno.xiandu.view.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.read.ReadActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By huzheng
 * Date 2020-02-11
 * Des 目录页
 */
public class DetailActivity extends BaseActivity {

    //数据
    @Autowired(name = "json")
    String json;
    private BookShelf book;
    private Bookbrack bookbrack = new Bookbrack();
    private ArrayList<Bookbrack> bookbracks = new ArrayList<>();

    //控件
    @BindView(R.id.header_bar_back_tv)
    TextView title;
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
    @BindView(R.id.catalog_main_booc_words)
    TextView bookWords;
    @BindView(R.id.AppBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.progress_bar_read)
    ProgressBar progressBar;
    @BindView(R.id.bt_coll)
    TextView collBt;
    @BindView(R.id.bt_read)
    View readBt;

    //返回
    @OnClick(R.id.header_bar_back_img)
    void onClick() {
        ActivityCompat.finishAfterTransition(this);
    }

    //收藏
    @OnClick(R.id.bt_coll)
    void coll() {
        if (SQL.getInstance().hasBookShelf(book)) {
            SQL.getInstance().delBookShelf(book);
//            Toaster.showToastCenter(this, "已移除");
//            collBt.setText("保存书架");
        } else {
            SQL.getInstance().addBookShelf(book);
//            Toaster.showToastCenter(this, "已保存");
//            collBt.setText("已保存");
        }

        if (SQL.getInstance().hasBookbrack(bookbrack)) {
            bookbracks.clear();
            bookbracks.add(bookbrack);
            SQL.getInstance().delBookbrack(bookbracks);
            Toaster.showToastCenter(this, "已从书架移除");
            collBt.setText("保存书架");
        } else {
            SQL.getInstance().addBookbrack(bookbrack, true);
            Toaster.showToastCenter(this, "成功加入书架");
            collBt.setText("已在书架");
        }
    }

    //立即阅读
    @OnClick(R.id.bt_read)
    void read() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                .withString("json", GsonUtil.objectToJson(book))
                .navigation();
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
            }
        });
        initData();
    }

    private void initData() {
        book = GsonUtil.gsonToObject(json, BookShelf.class);
        bookbrack.setContent_id(book.getContent_id());
        bookbrack.setBook_name(book.getBook_name());
        bookbrack.setPoster(book.getPoster());
        bookbrack.setWords_num(book.getWords_num());
        bookbrack.setChapter_name(book.getChapter_name());
        bookbrack.setChapter_id(book.getChapter_id());

        if (book == null) {
            Toaster.showToast(this, "无法获取书籍信息");
            return;
        }
        if (SQL.getInstance().hasBookbrack(bookbrack)) {
            collBt.setText("已保存");
        } else {
            collBt.setText("保存书架");
        }

        Glide.with(this).load(book.getPoster()).into(pic);
        title.setText(book.getBook_name());
        bookName.setText(book.getBook_name());
        bookAuthor.setText(book.getAuthor());
        bookCategory.setText(book.getCategory_name());
        bookIntro.setText(book.getIntroduction());
        bookWords.setText("字数：" + book.getWord_count() + "  热度：" + book.getPopularity());
//        if (SQL.getInstance(this).queryBookByName(book.getName()).size() > 0) {
//            enableCollBt();
//        }
    }

    //已收藏
    private void enableCollBt() {
        collBt.setEnabled(false);
        collBt.setTextColor(0xff888888);
        collBt.setText("已添加");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
