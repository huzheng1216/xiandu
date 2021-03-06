package com.inveno.xiandu.view.read;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.appbar.AppBarLayout;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.SystemBarUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.read.page.PageLoader;
import com.inveno.xiandu.view.read.page.PageView;
import com.inveno.xiandu.view.read.setting.BrightnessUtils;
import com.inveno.xiandu.view.read.setting.ReadSettingManager;
import com.inveno.xiandu.view.read.setting.ScreenUtils;
import com.inveno.xiandu.view.read.setting.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.VISIBLE;
import static com.inveno.android.ad.config.AdViewType.AD_READER_BETWEEN_TYPE;
import static com.inveno.android.ad.config.AdViewType.AD_READER_BOTTOM_TYPE;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BETWEEN;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BOTTOM;

/**
 * Created By huzheng
 * Date 2020/5/13
 * Des 阅读界面
 */
@Route(path = ARouterPath.ACTIVITY_CONTENT_MAIN)
public class ReadActivity extends BaseMVPActivity<ReadContract.Presenter>
        implements ReadContract.View {
    private static final String TAG = "ReadActivity";
    public static final int REQUEST_MORE_SETTING = 1;
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";

    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");

    private static final int WHAT_CATEGORY = 1;
    private static final int WHAT_CHAPTER = 2;


    @BindView(R.id.read_dl_slide)
    DrawerLayout mDlSlide;
    /*************top_menu_view*******************/
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout mAblTopMenu;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.read_tv_community)
    TextView mTvCommunity;
    @BindView(R.id.read_tv_brief)
    TextView mTvBrief;
    /***************content_view******************/
    @BindView(R.id.read_pv_page)
    PageView mPvPage;
    /***************bottom_menu_view***************************/
    @BindView(R.id.read_tv_page_tip)
    TextView mTvPageTip;
    /***************引导界面***************************/
    @BindView(R.id.attention_first_tag)
    View attenView;
    /***************bottom_ad***************************/
    @BindView(R.id.ad_bottom)
    FrameLayout adBottom;
    @BindView(R.id.layout_background)
    ViewGroup layoutBackground;
    /***** 章节广告 *****/
    @BindView(R.id.ad_chapter_layout)
    ViewGroup layoutChapterAD;
    @BindView(R.id.layout_ad_chapter)
    ViewGroup layoutChapter;
    @BindView(R.id.bt_continue)
    View btContinue;

    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout mLlBottomMenu;
    @BindView(R.id.read_tv_pre_chapter)
    TextView mTvPreChapter;
    @BindView(R.id.read_sb_chapter_progress)
    SeekBar mSbChapterProgress;
    @BindView(R.id.read_tv_next_chapter)
    TextView mTvNextChapter;
    @BindView(R.id.read_tv_category)
    TextView mTvCategory;
    @BindView(R.id.read_tv_night_mode)
    TextView mTvNightMode;
    /*    @BindView(R.id.read_tv_download)
        TextView mTvDownload;*/
    @BindView(R.id.read_tv_setting)
    TextView mTvSetting;
    /***************left slide*******************************/
    @BindView(R.id.read_iv_category)
    ListView mLvCategory;
    @BindView(R.id.left_layout)
    View mLeftLayout;
    /*****************view******************/
    private ReadSettingDialog mSettingDialog;
    private PageLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    private CategoryAdapter mCategoryAdapter;
    private BookShelf bookShelf;
    private Bookbrack bookbrack = new Bookbrack();
    //当前章节
    private ChapterInfo mChapterInfo;

    //心跳上报
    private Disposable subscribe;
    //每30秒切换底部广告
    private Disposable adBottomDisposable;
    private int adIndex = 5;//滑动几页展示广告
    private View chapterADView;//等待展示的章节广告
    private int currIndex;//滑动页数

    //控制屏幕常亮
    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case WHAT_CATEGORY:
                    mLvCategory.setSelection(mPageLoader.getChapterPos());
                    break;
                case WHAT_CHAPTER:
                    mPageLoader.openChapter();
                    break;
            }
        }
    };
    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };

    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;

            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                Log.d(TAG, "亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                Log.d(TAG, "亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                Log.d(TAG, "亮度模式为自动模式 值改变");
                BrightnessUtils.setDefaultBrightness(ReadActivity.this);
            } else {
                Log.d(TAG, "亮度调整 其他");
            }
        }
    };

    /***************params*****************/
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;

    private String mBookId;

    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    @Override
    protected ReadContract.Presenter bindPresenter() {
        return new ReadPresenter();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        String json = getIntent().getStringExtra("json");
        bookShelf = GsonUtil.gsonToObject(json, BookShelf.class);
        bookbrack.setContent_id(bookShelf.getContent_id());
        bookbrack.setBook_name(bookShelf.getBook_name());
        bookbrack.setPoster(bookShelf.getPoster());
        bookbrack.setWords_num(bookShelf.getWords_num());
        bookbrack.setChapter_name(bookShelf.getChapter_name());
        bookbrack.setChapter_id(bookShelf.getChapter_id());

        isCollected = false;
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();

        mBookId = bookShelf.getContent_id() + "";
        startBottomAd();
        startChapterAD();

        report();
    }

    /**
     * 执行章节广告
     */
    private void startChapterAD() {
        Integer optIndex = InvenoAdServiceHolder.getService().getPos(READER_BETWEEN);
        if (optIndex != null) {
            adIndex = optIndex;
        }
        getChapterAd();
    }

    /**
     * 执行30秒获取一次底部广告
     */
    private void startBottomAd() {
        //定时30秒调用一次
        adBottomDisposable = Flowable.interval(0, 30, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        getAdBottom();
                    }
                });
    }

    private void getAdBottom() {
        /**
         * 加载底部广告
         */
        InvenoAdServiceHolder.getService().requestInfoAd(READER_BOTTOM, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            AdModel adModel = new AdModel(wrapper);
            NormalAdViewHolder holder = ((NormalAdViewHolder) ADViewHolderFactory.create(ReadActivity.this, AD_READER_BOTTOM_TYPE));
            holder.onBindViewHolder(ReadActivity.this, wrapper.getAdValue(), 0);
            View view = holder.getViewGroup();
            adBottom.removeAllViews();
            adBottom.addView(view);
            adBottom.setVisibility(VISIBLE);
            return null;
        }).onFail((integer, s) -> {
            if (adBottom != null) {
                adBottom.setVisibility(GONE);
            }
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

    private void getChapterAd() {
        currIndex = 0;
        if (chapterADView != null) {
            layoutChapter.removeAllViews();
            layoutChapter.addView(chapterADView);
            layoutChapterAD.setBackgroundColor(isNightMode ? 0xff000000 : ContextCompat.getColor(this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
            layoutChapterAD.setVisibility(VISIBLE);
            chapterADView = null;
        }
        /**
         * 加载章节广告
         */
        InvenoAdServiceHolder.getService().requestInfoAd(READER_BETWEEN, this).onSuccess(wrapper -> {
            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            AdModel adModel = new AdModel(wrapper);
            NormalAdViewHolder holder = ((NormalAdViewHolder) ADViewHolderFactory.create(ReadActivity.this, AD_READER_BETWEEN_TYPE));
            holder.onBindViewHolder(ReadActivity.this, wrapper.getAdValue(), 0);
            //获取滑动页数配置
            adIndex = wrapper.getIndex();
            chapterADView = holder.getViewGroup();
            return null;
        }).onFail((integer, s) -> {
            if (layoutChapterAD != null) {
                layoutChapterAD.setVisibility(GONE);
            }
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        //设置标题
        toolbar.setTitle(bookShelf.getBook_name());
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //如果是首次进入，显示引导界面
        if (SPUtils.getInformain("first_reader_book", true)) {
            attenView.setVisibility(VISIBLE);
            attenView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SPUtils.setInformain("first_reader_book", false);
                    attenView.setVisibility(GONE);
                }
            });
        } else {
            attenView.setVisibility(GONE);
        }

        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPvPage.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(bookShelf);
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(true);
        mSettingDialog = new ReadSettingDialog(this, mPageLoader);
        mSettingDialog.setOnSettingListener(value -> {
            adBottom.setBackgroundColor(isNightMode ? 0xFF000000 : ContextCompat.getColor(ReadActivity.this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
            mLeftLayout.setBackgroundColor(isNightMode ? 0xFF000000 : ContextCompat.getColor(ReadActivity.this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
        });

        if (!SQL.getInstance().hasBookbrack(bookbrack)) {
            mTvBrief.setText("加入书架");
        } else {
            mTvBrief.setText("已在书架");
        }
        setUpAdapter();

        //夜间模式按钮的状态
        toggleNightMode();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

        //心跳
        startPostRead();

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }

        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ireader:keep bright");

        //隐藏StatusBar
        mPvPage.post(
                () -> hideSystemBar()
        );

        //继续阅读
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChapterAD.setVisibility(GONE);
            }
        });
        layoutChapterAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChapterAD.setVisibility(GONE);
            }
        });

        //初始化TopMenu
        initTopMenu();

        //初始化BottomMenu
        initBottomMenu();
    }

    private void startPostRead() {
        //记录是否在阅读
        lastTouch = System.currentTimeMillis();
        //定时50秒调用一次
        subscribe = Flowable.interval(0, 50, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        postReadTime();
                    }
                });
    }

    private void initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            mAblTopMenu.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
        }
        setUpToolbar(mToolbar);
        supportActionBar(mToolbar);
    }

    private void initBottomMenu() {
        //判断是否全屏
        if (ReadSettingManager.getInstance().isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLlBottomMenu.getLayoutParams();
            params.bottomMargin = ScreenUtils.getNavigationBarHeight();
            mLlBottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLlBottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            mLlBottomMenu.setLayoutParams(params);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: " + mAblTopMenu.getMeasuredHeight());
    }

    private void toggleNightMode() {
        if (isNightMode) {
            mTvNightMode.setText(StringUtils.getString(R.string.nb_mode_morning));
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_read_menu_morning);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            layoutBackground.setBackgroundColor(0xFF000000);
            adBottom.setBackgroundColor(0xFF000000);
            mLeftLayout.setBackgroundColor(0xFF000000);
        } else {
            mTvNightMode.setText(StringUtils.getString(R.string.nb_mode_night));
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_read_menu_night);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            layoutBackground.setBackgroundColor(ContextCompat.getColor(this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
            adBottom.setBackgroundColor(ContextCompat.getColor(ReadActivity.this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
            mLeftLayout.setBackgroundColor(ContextCompat.getColor(ReadActivity.this, ReadSettingManager.getInstance().getPageStyle().getBgColor()));
        }
    }

    private void setUpAdapter() {
        mCategoryAdapter = new CategoryAdapter();
        mLvCategory.setAdapter(mCategoryAdapter);
//        mLvCategory.setFastScrollEnabled(true);
    }

    // 注册亮度观察者
    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.showLog(TAG, "register mBrightObserver error! " + throwable);
        }
    }

    //解注册
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.showLog(TAG, "unregister BrightnessObserver error! " + throwable);
        }
    }

    int lastPos = -1;
    int lastChapterPos = 0;

    @SuppressLint("WrongConstant")
    @Override
    protected void initClick() {
        super.initClick();

        mPageLoader.setOnPageChangeListener(
                new PageLoader.OnPageChangeListener() {

                    @Override
                    public void onChapterChange(int pos) {
                        LogUtils.H("onChapterChange = " + pos + "   lastChapterPos:" + lastChapterPos);
                        mCategoryAdapter.setChapter(pos);
                        mSbChapterProgress.setProgress(pos);

                        if (pos > lastChapterPos) {
                            lastPos = 0;
                            currIndex++;
                        }
                        lastChapterPos = pos;
                        if (currIndex == adIndex) {
                            getChapterAd();
                        }
                    }

                    @Override
                    public void requestChapters(List<ChapterInfo> requestChapters) {
                        LogUtils.H("requestChapters = " + requestChapters.size());
                        mPresenter.loadChapter(mBookId, requestChapters);
                        mHandler.sendEmptyMessage(WHAT_CATEGORY);
                        //隐藏提示
                        mTvPageTip.setVisibility(GONE);
                    }

                    @Override
                    public void onCategoryFinish(List<ChapterInfo> chapters) {
                        LogUtils.H("onCategoryFinish = " + chapters.size());
                        for (ChapterInfo chapter : chapters) {
                            chapter.setChapter_name(StringUtils.convertCC(chapter.getChapter_name(), mPvPage.getContext()));
                        }
                        mCategoryAdapter.refreshItems(chapters);
                        mSbChapterProgress.setMax(Math.max(0, chapters.size()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mSbChapterProgress.setMin(1);
                        }
                    }

                    @Override
                    public void onPageCountChange(int count) {
                        LogUtils.H("onPageCountChange = " + count);
//                        mSbChapterProgress.setMax(Math.max(0, count - 1));
//                        mSbChapterProgress.setProgress(0);
//                        // 如果处于错误状态，那么就冻结使用
//                        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING
//                                || mPageLoader.getPageStatus() == PageLoader.STATUS_ERROR) {
//                            mSbChapterProgress.setEnabled(false);
//                        } else {
//                            mSbChapterProgress.setEnabled(true);
//                        }
                    }

                    @Override
                    public void onPageChange(int pos) {

                        if (pos == lastPos + 1) {
                            currIndex++;
                        }
                        LogUtils.H("onPageChange = " + pos + " lastPos:" + lastPos + " currIndex:" + currIndex + " adIndex" + adIndex);
                        lastPos = pos;
                        if (currIndex == adIndex) {
                            getChapterAd();
                        }
//                        mSbChapterProgress.post(
//                                () -> mSbChapterProgress.setProgress(pos)
//                        );
                    }
                }
        );

        mSbChapterProgress.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mLlBottomMenu.getVisibility() == VISIBLE) {
                            if (progress <= 0) {
                                progress = 1;
                            }
                            //显示标题
                            String chapterName = mCategoryAdapter.getItem(progress - 1).getChapter_name();
                            if (chapterName.length() > 12) {
                                chapterName = chapterName.substring(0, 11) + "...";
                            }
                            mTvPageTip.setText(chapterName);
//                            mTvPageTip.setText((progress) + "/" + (mSbChapterProgress.getMax()));
                            mTvPageTip.setVisibility(VISIBLE);
//                            mToolbar.setTitle(mCategoryAdapter.getItem(progress).getChapter_name());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //进行切换
                        int pagePos = mSbChapterProgress.getProgress() - 1;
                        if (pagePos < 0) {
                            pagePos = 0;
                        }
                        if (pagePos != mPageLoader.getChapterPos()) {
                            mPageLoader.skipToChapter(pagePos, 0);
                        }
                        //隐藏提示
                        mTvPageTip.setVisibility(GONE);
                    }
                }
        );

        mPvPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                stopHeart();
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }
        });

        mLvCategory.setOnItemClickListener(
                (parent, view, position, id) -> {
                    mDlSlide.closeDrawer(Gravity.START);
                    mPageLoader.skipToChapter(position, 0);
                }
        );

        mTvCategory.setOnClickListener(
                (v) -> {
                    //移动到指定位置
                    if (mCategoryAdapter.getCount() > 0) {
                        mLvCategory.setSelection(mPageLoader.getChapterPos());
                    }
                    //切换菜单
                    toggleMenu(true);
                    //打开侧滑动栏
                    mDlSlide.openDrawer(Gravity.START);
                }
        );
        mTvSetting.setOnClickListener(
                (v) -> {
                    toggleMenu(false);
                    mSettingDialog.show();
                }
        );

        mTvPreChapter.setOnClickListener(
                (v) -> {
                    if (mPageLoader.skipPreChapter()) {
                        mCategoryAdapter.setChapter(mPageLoader.getChapterPos());
                        String chapterName = mCategoryAdapter.getItem(mPageLoader.getChapterPos()).getChapter_name();
                        if (chapterName.length() > 12) {
                            chapterName = chapterName.substring(0, 11) + "...";
                        }
                        mTvPageTip.setText(chapterName);
                        mTvPageTip.setVisibility(VISIBLE);
                    }
                }
        );

        mTvNextChapter.setOnClickListener(
                (v) -> {
                    if (mPageLoader.skipNextChapter()) {
                        mCategoryAdapter.setChapter(mPageLoader.getChapterPos());
                        String chapterName = mCategoryAdapter.getItem(mPageLoader.getChapterPos()).getChapter_name();
                        if (chapterName.length() > 12) {
                            chapterName = chapterName.substring(0, 11) + "...";
                        }
                        mTvPageTip.setText(chapterName);
                        mTvPageTip.setVisibility(VISIBLE);
                    }
                }
        );

        mTvNightMode.setOnClickListener(
                (v) -> {
                    if (isNightMode) {
                        isNightMode = false;
                    } else {
                        isNightMode = true;
                    }
                    mPageLoader.setNightMode(isNightMode);
                    toggleNightMode();
                }
        );

        mTvBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShelf();
            }
        });

        mTvCommunity.setOnClickListener(
                (v) -> {
//                    Intent intent = new Intent(this, CommunityActivity.class);
//                    startActivity(intent);
                }
        );

        mSettingDialog.setOnDismissListener(
                dialog -> hideSystemBar()
        );
    }

    /**
     * 保存到书架
     */
    private void addShelf() {
//        if (SQL.getInstance().hasBookShelf(bookShelf)) {
//            SQL.getInstance().delBookShelf(bookShelf);
////            ArrayList<Bookbrack> bookbracks = new ArrayList<>();
////            bookbracks.add(bookbrack);
////            SQL.getInstance().delBookbrack(bookbracks);
//            Toaster.showToastCenter(ReadActivity.this, "已从书架移除");
//            mTvBrief.setText("保存书架");
//        } else {
        if (!SQL.getInstance().hasBookbrack(bookShelf.getContent_id())) {
            SQL.getInstance().addBookShelf(bookShelf);
            SQL.getInstance().addBookbrack(bookbrack, true);
            Toaster.showToastCenter(ReadActivity.this, "成功加入书架");
            mTvBrief.setText("已在书架");
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        hideSystemBar();
        if (mAblTopMenu.getVisibility() == VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    private void showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this);
        }
    }

    private void hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this);
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();
        hideSystemBar();

        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mAblTopMenu.startAnimation(mTopOutAnim);
            mLlBottomMenu.startAnimation(mBottomOutAnim);
            mAblTopMenu.setVisibility(GONE);
            mLlBottomMenu.setVisibility(GONE);
            mTvPageTip.setVisibility(GONE);

//            if (hideStatusBar) {
//                hideSystemBar();
//            }
        } else {
            mAblTopMenu.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.VISIBLE);
            mAblTopMenu.startAnimation(mTopInAnim);
            mLlBottomMenu.startAnimation(mBottomInAnim);

//            showSystemBar();
        }
    }

    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    @Override
    protected void processLogic() {
        super.processLogic();
        // 如果是已经收藏的，那么就从数据库中获取目录
//        if (isCollected) {
//            Disposable disposable = BookRepository.getInstance()
//                    .getBookChaptersInRx(mBookId)
//                    .compose(RxUtils::toSimpleSingle)
//                    .subscribe(
//                            (bookChapterBeen, throwable) -> {
//                                // 设置 CollBook
//                                mPageLoader.getCollBook().setBookChapters(bookChapterBeen);
//                                // 刷新章节列表
//                                mPageLoader.refreshChapterList();
//                                // 如果是网络小说并被标记更新的，则从网络下载目录
//                                if (mCollBook.isUpdate() && !mCollBook.isLocal()) {
//                                    mPresenter.loadCategory(mBookId);
//                                }
//                                LogUtils.e(throwable);
//                            }
//                    );
//            addDisposable(disposable);
//        } else {
        // 从网络中获取目录
        mPresenter.loadCategory(mBookId);
//        }
    }

    /***************************view************************************/
    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showCategory(List<ChapterInfo> bookChapters) {
        if (bookChapters == null) {
            Toaster.showToastCenter(this, "章节列表获取失败");
            finish();
            return;
        }
        //根据章节id，获取章节位置
        int chapter_id = getIntent().getIntExtra("capter", 0);
        int chapterNum = getIntent().getIntExtra("chapter_num", -1);
        int wordsNum = getIntent().getIntExtra("words_num", 0);
        if (chapterNum > 0) {
            mChapterInfo = bookChapters.get(chapterNum);
            mPageLoader.skipToChapter(chapterNum, 0);
        } else {
            mChapterInfo = bookChapters.get(0);
            if (chapter_id > 0) {
                for (int i = 0; i < bookChapters.size(); i++) {
                    if (chapter_id == bookChapters.get(i).getChapter_id()) {
                        mChapterInfo = bookChapters.get(i);
                        mPageLoader.skipToChapter(i, wordsNum);
                        break;
                    }
                }
            }
        }

        mPageLoader.getCollBook().setBookChapters(bookChapters);
        mPageLoader.refreshChapterList();

        // 如果是目录更新的情况，那么就需要存储更新数据
//        if (mCollBook.isUpdate() && isCollected) {
//            BookRepository.getInstance()
//                    .saveBookChaptersWithAsync(bookChapters);
//        }
    }

    @Override
    public void finishChapter() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER);
        }
        // 当完成章节的时候，刷新列表
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorChapter() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mDlSlide.isDrawerOpen(Gravity.START)) {
            mDlSlide.closeDrawer(Gravity.START);
            return;
        }

//        if (!mCollBook.isLocal() && !isCollected
//                && !mCollBook.getBookChapters().isEmpty()) {
//            AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("加入书架")
//                    .setMessage("喜欢本书就加入书架吧")
//                    .setPositiveButton("确定", (dialog, which) -> {
//                        //设置为已收藏
//                        isCollected = true;
//                        //设置阅读时间
//                        mCollBook.setLastRead(StringUtils.
//                                dateConvert(System.currentTimeMillis(), Const.FORMAT_BOOK_DATE));
//
////                        BookRepository.getInstance()
////                                .saveCollBookWithAsync(mCollBook);
//
//                        exit();
//                    })
//                    .setNegativeButton("取消", (dialog, which) -> {
//                        exit();
//                    }).create();
//            alertDialog.show();
//        } else {
//            exit();
//        }
        exit();
    }

    // 退出
    private void exit() {
        // 返回给BookDetail。
//        Intent result = new Intent();
//        result.putExtra(BookDetailActivity.RESULT_IS_COLLECTED, isCollected);
//        setResult(Activity.RESULT_OK, result);
        // 退出
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
        ReportManager.INSTANCE.readBookStart("", "", 0, bookShelf.getContent_id());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();

        //启动心跳
        if (subscribe.isDisposed()) {
            startPostRead();
        }
        //隐藏StatusBar
        mPvPage.post(
                () -> hideSystemBar()
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
//        if (isCollected) {
        mPageLoader.saveRecord();
//        }
    }

    @Override
    protected void onStop() {
        ReportManager.INSTANCE.readBookStartEnd(this, ServiceContext.userService().getUserPid());
        super.onStop();
        unregisterBrightObserver();
        //结束心跳
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }

    @Override
    public void finish() {
        if (!SQL.getInstance().hasBookbrack(bookShelf.getContent_id())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("问题：");
            builder.setMessage("加入书架方便下次阅读?");
            //设置正面按钮
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addShelf();
                    dialog.dismiss();
                    ReadActivity.super.finish();
                }
            });
            //设置反面按钮
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ReadActivity.super.finish();
                }
            });
            builder.show();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

        mHandler.removeMessages(WHAT_CATEGORY);
        mHandler.removeMessages(WHAT_CHAPTER);

        mPageLoader.closeBook();
        mPageLoader = null;

        //结束心跳
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        //结束底部广告
        if (adBottomDisposable != null && !adBottomDisposable.isDisposed()) {
            adBottomDisposable.dispose();
        }
        //上报阅读进度
//        DDManager.getInstance().postReadProgress(bookShelf.getContent_id(), bookShelf.get)
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Consumer<BaseRequest>() {
//                    @Override
//                    public void accept(BaseRequest baseRequest) throws Exception {
//
//                    }
//                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isVolumeTurnPage = ReadSettingManager
                .getInstance().isVolumeTurnPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToPrePage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemBarUtils.hideStableStatusBar(this);
        if (requestCode == REQUEST_MORE_SETTING) {
            boolean fullScreen = ReadSettingManager.getInstance().isFullScreen();
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen;
                // 刷新BottomMenu
                initBottomMenu();
            }

            // 设置显示状态
            if (isFullScreen) {
                SystemBarUtils.hideStableNavBar(this);
            } else {
                SystemBarUtils.showStableNavBar(this);
            }
        }
    }

    /**
     * 上报心跳
     */
    private void postReadTime() {
        long now = System.currentTimeMillis();
        if (now - lastTouch < 60000) {
            DDManager.getInstance().postReadTime(mBookId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Observer<BaseRequest>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest baseRequest) {
                            LogUtils.H("上传心跳：" + baseRequest.getCode() + "-" + baseRequest.getMessage());
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.H("无法上传心跳");
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            LogUtils.H("未阅读，心跳任务暂停！");
            subscribe.dispose();
        }
    }

    /**
     * 记录是否还在阅读
     */
    long lastTouch;

    private void stopHeart() {
        lastTouch = System.currentTimeMillis();
        //如果心跳已经停止了，则重启心跳
        if (subscribe.isDisposed()) {
            startPostRead();
        }
    }

    private void report() {
        ReportManager.INSTANCE.reportPageImp(10, "", this, ServiceContext.userService().getUserPid());
    }

}
