package com.inveno.xiandu.view.detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.BitmapUtil;
import com.inveno.xiandu.utils.GlideBlurformation;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.adapter.RelevantBookAdapter;
import com.inveno.xiandu.view.custom.MScrollView;
import com.inveno.xiandu.view.read.CategoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.AdViewType.AD_BOOK_DETAIL_POP_TYPE;
import static com.inveno.android.ad.config.AdViewType.AD_BOOK_DETAIL_TYPE;
import static com.inveno.android.ad.config.ScenarioManifest.BOOK_DETAIL;

/**
 * @author yongji.wang
 * @date 2020/6/19 16:10
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_DETAIL_MAIN)
public class BookDetailActivity extends BaseActivity {
    //数据
    @Autowired(name = "json")
    String json;
    private BookShelf book;
    private Bookbrack bookbrack = new Bookbrack();
    private ArrayList<Bookbrack> bookbracks = new ArrayList<>();
    private boolean isTelescopic = false;//是否展开简介
    private boolean isOpenFirstCapter = false;//是否展开简介


    //控件
    @BindView(R.id.book_detail_coll)
    TextView book_detail_coll;//收藏

    @BindView(R.id.book_detail_read)
    TextView book_detail_read;//立即阅读

    @BindView(R.id.book_detail_poster)
    ImageView book_detail_poster;//封面

    @BindView(R.id.book_detail_goss_bg)
    ImageView book_detail_goss_bg;//高斯模糊背景

    @BindView(R.id.book_detail_bookname)
    TextView book_detail_bookname;//书名

    @BindView(R.id.book_detail_author)
    TextView book_detail_author;//作者

    @BindView(R.id.book_detail_type)
    TextView book_detail_type;//书籍分类

    @BindView(R.id.book_detail_score)
    TextView book_detail_score;//评分

    @BindView(R.id.book_detail_popularity)
    TextView book_detail_popularity;//人气

    @BindView(R.id.book_detail_unit)
    TextView book_detail_unit;//人气单位

    @BindView(R.id.tvContent)
    TextView tvContent;//简介

    @BindView(R.id.tvTelescopic)
    TextView tvTelescopic;//展開

    @BindView(R.id.first_capter_title)
    TextView first_capter_title;//第一章标题

    @BindView(R.id.first_capter_msg)
    TextView first_capter_msg;//第一章内容

    @BindView(R.id.open_first_more)
    TextView open_first_more;//展开第一章

    @BindView(R.id.book_detail_bottom_recyclerview)
    RecyclerView book_detail_bottom_recyclerview;

    @BindView(R.id.ad_viewgroup)
    LinearLayout ad_viewgroup;

    @BindView(R.id.ad_bottom_viewgroup)
    LinearLayout ad_bottom_viewgroup;

    @BindView(R.id.book_detail_scrollview)
    MScrollView book_detail_scrollview;

    @BindView(R.id.book_capter)
    RelativeLayout book_capter;

    @BindView(R.id.second_titleBar)
    RelativeLayout second_titleBar;

    @BindView(R.id.tittle_bar_layout)
    RelativeLayout tittle_bar_layout;

    @BindView(R.id.second_titleBar_title)
    TextView second_titleBar_title;

    LinearLayout ad_pop_viewgroup;

    private AdModel adModel;
    private AdModel adBottomModel;

    private RelevantBookAdapter relevantBookAdapter;
    private List<BookShelf> bookShelfs = new ArrayList<>();
    private List<ChapterInfo> chapterInfos;
    private PopupWindow popupWindow;
    private View contentView;
    private TextView pop_directory_capter_num;
    private TextView pop_directory_capter_order;
    private ListView pop_directory_category_list;
    private CategoryAdapter mCategoryAdapter;

    private boolean isReverse = false;

    private boolean isShowSecondGoss = false;
    private boolean isShowSecondWhite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);
        setStatusBar(R.color.white, true);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SQL.getInstance().hasBookbrack(bookbrack)) {
            book_detail_coll.setText("已在书架");
        } else {
            book_detail_coll.setText("加入书架");
        }
    }

    private void initData() {
        book = GsonUtil.gsonToObject(json, BookShelf.class);
        if (book == null) {
            showErrorPopwindow(NO_BOOK_ERROR, new OnClickListener() {
                @Override
                public void onBackClick() {

                }

                @Override
                public void onRefreshClick() {
                    finish();
                }
            });
        }

        if (book == null) {
            Toaster.showToast(this, "无法获取书籍信息");
            return;
        }
        bookbrack.setContent_id(book.getContent_id());
        bookbrack.setBook_name(book.getBook_name());
        bookbrack.setPoster(book.getPoster());
        bookbrack.setWords_num(book.getWords_num());
        bookbrack.setChapter_name(book.getChapter_name());
        bookbrack.setChapter_id(book.getChapter_id());

        Glide.with(this).load(book.getPoster()).into(book_detail_poster);
        GlideUtils.LoadImageGoss(this, book.getPoster(), book_detail_goss_bg);

        book_detail_bookname.setText(book.getBook_name());
        book_detail_author.setText(book.getAuthor() + " 著");
//        "穿越宫斗-连载-38万字"
        String bookType = book.getCategory_name();
        if (book.getBook_status() == 0) {
            bookType = bookType + "-连载";
        } else {
            bookType = bookType + "-完结";
        }
        String wordsCountStr = "";
        if (book.getWord_count() < 1000) {
            wordsCountStr = String.format(bookType + "-%s字", book.getWord_count());
        } else if (book.getWord_count() >= 1000 && book.getWord_count() < 10000) {
            wordsCountStr = String.format(bookType + "-%s千字", book.getWord_count() / 1000);
        } else {
            wordsCountStr = String.format(bookType + "-%s万字", book.getWord_count() / 10000);
        }
        book_detail_type.setText(wordsCountStr);

        book_detail_score.setText(String.valueOf(book.getScore()));
        String popularityStr = "";
        if (book.getPopularity() < 1000) {
            popularityStr = String.format("%s", book.getPopularity());
            book_detail_unit.setText("+");
        } else if (book.getPopularity() >= 1000 && book.getPopularity() < 10000) {
            popularityStr = String.format("%s", book.getPopularity() / 1000);
            book_detail_unit.setText("千+");
        } else {
            popularityStr = String.format("%s", book.getPopularity() / 10000);
            book_detail_unit.setText("万+");
        }
        book_detail_popularity.setText(popularityStr);

        tvContent.setText(book.getIntroduction());
        /**
         *由于TextView渲染是需要时间的，
         * 如果在获取到TextView并setText之后立马进行判断，会发现此时TextView还没有绘制出来，拿到的属性均为初始值，
         * 比如前面函数中调用的getWidth返回为0，这样显然是不对的。所以要在TextView.post方法中执行。
         */
        tvContent.post(new Runnable() {
            @Override
            public void run() {
                boolean b = isTextView(tvContent);
                if (b) {
                    tvTelescopic.setVisibility(View.VISIBLE);
                } else {
                    tvTelescopic.setVisibility(View.GONE);

                }
            }
        });
        loadCapter();

        book_detail_bottom_recyclerview = findViewById(R.id.book_detail_bottom_recyclerview);
        relevantBookAdapter = new RelevantBookAdapter(this, bookShelfs);
        book_detail_bottom_recyclerview.setAdapter(relevantBookAdapter);
        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        dataLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        book_detail_bottom_recyclerview.setLayoutManager(dataLayoutManager);
        relevantBookAdapter.setOnitemClickListener(new RelevantBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                        .withString("json", GsonUtil.objectToJson(bookShelfs.get(position)))
                        .navigation();
                clickReport(bookShelfs.get(position).getContent_id());
            }
        });
        book_detail_bottom_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    impReport();
                }
            }
        });
        book_detail_scrollview.setOnScrollBottomListener(new MScrollView.OnScrollBottomListener() {
            @Override
            public void scrollBottom() {
                impReport();
            }
        });

        book_detail_scrollview.setScrollViewListener(new MScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (overScreen(tittle_bar_layout)) {
                    if (overScreen(book_capter)) {
                        Log.i("wyjjjjj", "目录不可见，");
                        if (!isShowSecondWhite) {
                            //改变背景
                            second_titleBar.setBackgroundColor(Color.parseColor("#ffffff"));
                            second_titleBar_title.setText("书籍详情");
                            isShowSecondWhite = true;
                            isShowSecondGoss = false;

                        }
                    } else {
                        Log.i("wyjjjjj", "目录可见，");
                        if (!isShowSecondGoss) {
                            second_titleBar.setVisibility(View.VISIBLE);
                            isShowSecondGoss = true;
                            isShowSecondWhite = false;
                            second_titleBar_title.setText("");
                            Glide.with(BookDetailActivity.this)
                                    .load(book.getPoster())
                                    .apply(RequestOptions.bitmapTransform(new GlideBlurformation(BookDetailActivity.this)))
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            BitmapDrawable bd = (BitmapDrawable) resource;
                                            Bitmap bm = bd.getBitmap();

                                            Bitmap bitmap = BitmapUtil.cutBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight() / 3);

                                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                            second_titleBar.setBackground(drawable);
                                        }
                                    });
                        }
                    }

                } else {
                    Log.i("wyjjjjj", "高斯可见，");
                    isShowSecondGoss = false;
                    isShowSecondWhite = false;
                    second_titleBar.setVisibility(View.GONE);
                }
            }
        });

        getRelevantBook();
        initDirectoryPopwindow();

        loadAd();
        report();
    }

    private boolean isTextView(TextView textView) {

        if (textView.getLineCount() > 4) {
            return true;
        }
        return false;
    }

    private boolean isFirstCapter(TextView textView) {

        if (textView.getLineCount() > 10) {
            return true;
        }
        return false;
    }

    private void initDirectoryPopwindow() {
        //加载弹出框的布局
        contentView = LayoutInflater.from(this).inflate(R.layout.pop_directory_view, null);
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, height * 11 / 15);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 按下android回退物理键 PopipWindow消失解决

        //初始化popwindow的控件
        initPopView();
    }

    private void initPopView() {
        pop_directory_capter_num = contentView.findViewById(R.id.pop_directory_capter_num);
        pop_directory_capter_order = contentView.findViewById(R.id.pop_directory_capter_order);
        pop_directory_category_list = contentView.findViewById(R.id.pop_directory_category_list);
        ad_pop_viewgroup = contentView.findViewById(R.id.ad_pop_viewgroup);
    }

    private void openPopWindow() {
        //从底部显示
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        //添加数据填充
        setData();
        loadPopAd();
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f, BookDetailActivity.this);
            }
        });
        setBackgroundAlpha(0.5f, this);
    }

    private void setData() {
        String capterStr;
        if (book.getBook_status() == 0) {
            capterStr = "连载中 ";
        } else {
            capterStr = "已完结 ";
        }
        capterStr = String.format(capterStr + "共%s章", book.getBookChapters().size());
        pop_directory_capter_num.setText(capterStr);
        pop_directory_capter_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReverse) {
                    pop_directory_capter_order.setText("倒序");
                } else {
                    pop_directory_capter_order.setText("正序");
                }
                isReverse = !isReverse;
                Collections.reverse(chapterInfos);
                mCategoryAdapter.refreshItems(chapterInfos);
            }
        });
        mCategoryAdapter = new CategoryAdapter();
        pop_directory_category_list.setAdapter(mCategoryAdapter);
        pop_directory_category_list.setFastScrollEnabled(true);

        mCategoryAdapter.refreshItems(book.getBookChapters());

        pop_directory_category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                        .withString("json", GsonUtil.objectToJson(book))
                        .withInt("capter", position)
                        .navigation();
            }
        });
    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    //获取章节和初始化目录
    private void loadCapter() {
        DDManager.getInstance().getChapterList(book.getContent_id() + "", 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRequest<BookChapter>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRequest<BookChapter> bookChapterBaseRequest) {
                        chapterInfos = bookChapterBaseRequest.getData().getChapter_list();
                        book.setBookChapters(chapterInfos);
                        refreshCapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //获取第一章内容
    private void getFirstCapter() {
        DDManager.getInstance().getChapterInfo(book.getContent_id() + "", book.getBookChapters().get(0).getChapter_id() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRequest<ChapterInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRequest<ChapterInfo> chapterInfoBaseRequest) {
                        ChapterInfo chapterInfo = chapterInfoBaseRequest.getData();
                        first_capter_title.setVisibility(View.VISIBLE);
                        first_capter_msg.setVisibility(View.VISIBLE);
                        open_first_more.setVisibility(View.VISIBLE);

                        first_capter_title.setText(chapterInfo.getChapter_name());
                        first_capter_msg.setText(chapterInfo.getContent());
                        first_capter_msg.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean b = isFirstCapter(first_capter_msg);
                                if (b) {
                                    open_first_more.setText("展开更多");
                                    isOpenFirstCapter = false;
                                } else {
                                    open_first_more.setText("继续阅读下一章");
                                    isOpenFirstCapter = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void refreshCapter() {
        if (book != null && book.getBookChapters() != null) {
            //刷新目录数据
            if (book.getBookChapters().size() > 0) {
                getFirstCapter();
            } else {
                Toaster.showToastShort(this, "目录准备中，请稍等");
                first_capter_title.setVisibility(View.GONE);
                first_capter_msg.setVisibility(View.GONE);
                open_first_more.setVisibility(View.GONE);
            }
        }
    }

    private void getRelevantBook() {
        APIContext.getBookCityAPi().getRelevantBook(book.getContent_id())
                .onSuccess(new Function1<BookShelfList, Unit>() {
                    @Override
                    public Unit invoke(BookShelfList bookShelfList) {
                        bookShelfs = bookShelfList.getNovel_list();
                        relevantBookAdapter.setsData(bookShelfs);
                        impReport();
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
    }

    //返回
    @OnClick(R.id.book_detail_bar_back)
    void onClick() {
        ActivityCompat.finishAfterTransition(this);
    }

    //返回
    @OnClick(R.id.second_titleBar_back)
    void onClickSecondBack() {
        ActivityCompat.finishAfterTransition(this);
    }

    //收藏
    @OnClick(R.id.book_detail_coll)
    void coll() {
        if (!SQL.getInstance().hasBookShelf(book)) {
            SQL.getInstance().addBookShelf(book);
        }

        if (!SQL.getInstance().hasBookbrack(bookbrack)) {
            SQL.getInstance().addBookbrack(bookbrack);
            Toaster.showToastCenter(this, "成功加入书架");
            book_detail_coll.setText("已在书架");
        }
    }

    //立即阅读
    @OnClick(R.id.book_detail_read)
    void read() {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                .withString("json", GsonUtil.objectToJson(book))
                .navigation();
    }

    //展开简介
    @OnClick(R.id.tvTelescopic)
    void tvTelescopic() {
        if (!isTelescopic) {
            //隐藏展开按钮
            tvTelescopic.setText("收起");
            //TextView行数显示最大
            tvContent.setMaxLines(Integer.MAX_VALUE);
            isTelescopic = true;
        } else {
            //显示展开按钮
            tvTelescopic.setText("展开");
            //TextView行数显示3行
            tvContent.setMaxLines(4);
            isTelescopic = false;
        }
    }

    //打开目录
    @OnClick(R.id.book_capter)
    void open_book_capter() {
        if (book.getBookChapters() != null && book.getBookChapters().size() > 0) {
            openPopWindow();
        } else {
            Toaster.showToastShort(this, "目录准备中，请稍等");
        }
    }

    //展开第一章
    @OnClick(R.id.open_first_more)
    void openFirst() {
        if (!isOpenFirstCapter) {
            //隐藏展开按钮
            open_first_more.setText("继续阅读下一章");
            //TextView行数显示最大
            first_capter_msg.setMaxLines(Integer.MAX_VALUE);
            isOpenFirstCapter = true;
        } else {
            //这里需要跳转到小说阅读
            ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                    .withString("json", GsonUtil.objectToJson(book))
                    .withInt("capter", 1)
                    .navigation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        InvenoAdServiceHolder.getService().requestInfoAd(BOOK_DETAIL, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModel = setAdData(ad_viewgroup, wrapper, AD_BOOK_DETAIL_TYPE);
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();

        InvenoAdServiceHolder.getService().requestInfoAd(BOOK_DETAIL, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adBottomModel = setAdData(ad_bottom_viewgroup, wrapper, AD_BOOK_DETAIL_TYPE);
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

    private AdModel setAdData(ViewGroup viewGroup, IndexedAdValueWrapper wrapper, int type) {
        AdModel adModel = new AdModel(wrapper);
        NormalAdViewHolder holder = ((NormalAdViewHolder) ADViewHolderFactory.create(BookDetailActivity.this, type));
        holder.onBindViewHolder(BookDetailActivity.this, wrapper.getAdValue(), 0);
        View view = holder.getViewGroup();
        viewGroup.addView(view);
        viewGroup.setVisibility(View.VISIBLE);
        return adModel;
    }

    private void loadPopAd() {
        InvenoAdServiceHolder.getService().requestInfoAd(BOOK_DETAIL, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            if (ad_pop_viewgroup != null) {
                setAdData(ad_pop_viewgroup, wrapper, AD_BOOK_DETAIL_POP_TYPE);
            }
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

    private void report() {
        ReportManager.INSTANCE.reportPageImp(11, "", this, ServiceContext.userService().getUserPid());
    }

    private boolean overScreen(View childView) {
        Rect scrollBounds = new Rect();
        book_detail_scrollview.getHitRect(scrollBounds);
        if (childView.getLocalVisibleRect(scrollBounds)) {//可见
            return false;
        } else {//完全不可见
            return true;
        }
    }

    private void clickReport(long contentId) {
        ReportManager.INSTANCE.reportBookClick(11, "", "", 9,
                0, contentId, BookDetailActivity.this, ServiceContext.userService().getUserPid());
    }

    private void impReport(int first, int last) {
        int size = bookShelfs.size();
        if (size > 0 && size > last) {
            for (int i = first; i <= last; i++) {
                BookShelf bookShelf = bookShelfs.get(i);
                Log.i("ReportManager", "name:" + bookShelf.getBook_name());
                long contentId = bookShelf.getContent_id();
                ReportManager.INSTANCE.reportBookImp(11, "", "", 9,
                        0, contentId, BookDetailActivity.this, ServiceContext.userService().getUserPid());
            }
        }
    }

    private void impReport() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) book_detail_bottom_recyclerview.getLayoutManager();
        if (layoutManager != null) {
            impReport(layoutManager.findFirstCompletelyVisibleItemPosition(), layoutManager.findLastCompletelyVisibleItemPosition());
        }
    }

}
