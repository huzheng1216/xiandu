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
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By huzheng
 * Date 2020-02-11
 * Des 目录页
 */
@Route(path = ARouterPath.ACTIVITY_DETAIL_MAIN)
public class DetailActivity extends BaseActivity {

    //数据
    @Autowired(name = "json")
    protected String json;
    private BookShelf book;

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
//        boolean b = SQL.getInstance(this).insertBook(book);
//        if (b) {
//            Toaster.showToastCenter(this, "已添加到书架");
//            enableCollBt();
//        }
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

        if (book == null) {
            Toaster.showToast(this, "无法获取书籍信息");
            return;
        }
        Glide.with(this).load(book.getPoster()).into(pic);
        title.setText(book.getBook_name());
        bookName.setText(book.getBook_name());
        bookAuthor.setText(book.getAuthor());
        bookCategory.setText(book.getCategory_name());
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
