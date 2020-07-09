package com.inveno.xiandu.view.main.my;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdReadTrackModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ReadTrack;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.adapter.ReadFootprintAdapter;
import com.inveno.xiandu.view.custom.MSwipeRefreshLayout;
import com.inveno.xiandu.view.custom.SwipeItemLayout;
import com.inveno.xiandu.view.dialog.IosTypeDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.READ_FOOT_TRACE;

/**
 * @author yongji.wang
 * @date 2020/6/9 17:13
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_READ_FOOTPRINT)
public class ReadFootprintActivity extends TitleBarBaseActivity {

    private LinearLayout no_book_show;

    @BindView(R.id.bookbrack_delete_all_line)
    LinearLayout bookbrack_delete_all_line;

    @BindView(R.id.to_book_store)
    TextView to_book_store;

    private ReadFootprintAdapter readFootprintAdapter;

    private IosTypeDialog iosTypeDialog;

    private AdReadTrackModel adBookModel;
    private int adCount;
    private int adIndex;

    private RecyclerView footprint_recycle;


    @Override
    public String getCenterText() {
        return "阅读足迹";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_read_footprint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
        ButterKnife.bind(this, this);
        report();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void initView() {
        super.initView();
        footprint_recycle = findViewById(R.id.footprint_recycle);
        footprint_recycle.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        readFootprintAdapter = new ReadFootprintAdapter(this);

        no_book_show = findViewById(R.id.no_book_show);
        // 设置adapter
        footprint_recycle.setAdapter(readFootprintAdapter);
        // 设置Item添加和移除的动画
        footprint_recycle.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        footprint_recycle.setLayoutManager(dataLayoutManager);
        //TODO 这里可能内存泄漏
        footprint_recycle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                impReport();
            }
        });
        footprint_recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    impReport();
                    loadMoreAd();
                }
            }
        });

        readFootprintAdapter.setShelfAdapterListener(new ReadFootprintAdapter.ShelfAdapterListener() {

            @Override
            public void onBookReadContinue(ReadTrack readTrack) {
            }

            @Override
            public void onBookDelete(ReadTrack readTrack) {
                IosTypeDialog.Builder builder = new IosTypeDialog.Builder(ReadFootprintActivity.this);
                builder.setContext("确定要删除这本书？");
                builder.setTitle("提示");
                builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog = null;

                        List<ReadTrack> readTracks = readFootprintAdapter.deleteSelect();
                        if (readTracks.size() < 1) {
                            no_book_show.setVisibility(View.VISIBLE);
                        }
                    }
                });
                builder.setRightButton("取消", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog = null;
                    }
                });

                iosTypeDialog = builder.create();

                iosTypeDialog.show();
                setDialogWindowAttr(iosTypeDialog);
            }

            @Override
            public void onBookClick(ReadTrack readTrack) {
                // TODO: 2020/6/17  去数据库查书，查到了就跳转，没查到就请求后跳转
                clickReport(readTrack.getContent_id());
                APIContext.getBookCityAPi().getBook(readTrack.getContent_id())
                        .onSuccess(new Function1<BookShelf, Unit>() {
                            @Override
                            public Unit invoke(BookShelf bookShelf) {
                                ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                                        .withString("json", GsonUtil.objectToJson(bookShelf))
                                        .withInt("capter", readTrack.getChapter_id())
                                        .withInt("words_num", readTrack.getWords_num())
                                        .navigation();
                                return null;
                            }
                        })
                        .onFail(new Function2<Integer, String, Unit>() {
                            @Override
                            public Unit invoke(Integer integer, String s) {
                                Toaster.showToastCenterShort(ReadFootprintActivity.this, "获取小说失败");
                                return null;
                            }
                        }).execute();
            }

            @Override
            public void onBookLongClick(ReadTrack bookbrack, View parent) {
                Animation animBottomIn = AnimationUtils.loadAnimation(ReadFootprintActivity.this, R.anim.bottom_in);
                animBottomIn.setDuration(500);
                bookbrack_delete_all_line.setVisibility(View.VISIBLE);
                bookbrack_delete_all_line.startAnimation(animBottomIn);
                readFootprintAdapter.setSelect(true);
            }

            @Override
            public void onFooterClick() {
            }
        });

        MSwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //从网络加载书籍
                APIContext.readTrackApi().getReadTrackList(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid())
                        .onSuccess(new Function1<List<ReadTrack>, Unit>() {
                            @Override
                            public Unit invoke(List<ReadTrack> readTracks) {
                                swipeRefreshLayout.setRefreshing(false);
//                            //同步数据
                                syncData(readTracks);
                                return null;
                            }
                        })
                        .onFail(new Function2<Integer, String, Unit>() {
                            @Override
                            public Unit invoke(Integer integer, String s) {
                                swipeRefreshLayout.setRefreshing(false);
                                return null;
                            }
                        }).execute();
            }
        });
        getData();
        loadAd();

    }

    private void getData() {
        initData();
        //从网络加载书籍
        APIContext.readTrackApi().getReadTrackList(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid())
                .onSuccess(new Function1<List<ReadTrack>, Unit>() {
                    @Override
                    public Unit invoke(List<ReadTrack> readTracks) {
//                            //同步数据
                        syncData(readTracks);
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

    /**
     * 对比书架数据
     *
     * @param data
     */
    private void syncData(List<ReadTrack> data) {
//        SQL.getInstance().insertOrReplaceBookbrack(data);
        initData();

        for (ReadTrack readTrack : data) {
            //后台有，本地没有，需要添加到本地
            if (!SQL.getInstance().hasReadTrack(readTrack.getContent_id())) {
                SQL.getInstance().addReadTrack(readTrack);
                initData();
            }
        }
//        if (SQL.getInstance().getAllReadTrack().size() != data.size()) {
//            SQL.getInstance().insertOrReplaceReadTrack(data);
//            initData();
//        }
    }

    private void initData() {
        List<ReadTrack> readTracks = SQL.getInstance().getAllReadTrack();
        if (readTracks.size() < 1) {
            no_book_show.setVisibility(View.VISIBLE);
        } else {
            no_book_show.setVisibility(View.GONE);
        }
        //TODO 加广告
        if (adBookModel != null) {
            int index = adBookModel.getIndex();
            if (readTracks.size() >= index) {
                readTracks.add(index, adBookModel);
            }
        }
        readFootprintAdapter.setData(readTracks);
    }

    //在dialog.show()之后调用
    public void setDialogWindowAttr(Dialog dlg) {
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.8); //设置宽度
        dlg.getWindow().setAttributes(lp);
    }

    @OnClick(R.id.to_book_store)
    void to_book_store() {
        Intent intent = new Intent();
        setResult(MineFragment.RESULT_READ_PRE, intent);
        finish();
    }

    @OnClick(R.id.bookbrack_delete_all_cancel)
    void delete_cannel() {
        Animation animBottomOut = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
        animBottomOut.setDuration(500);
        bookbrack_delete_all_line.setVisibility(View.GONE);
        bookbrack_delete_all_line.startAnimation(animBottomOut);

        readFootprintAdapter.setSelect(false);
    }

    @OnClick(R.id.bookbrack_delete_all_select_all)
    void delete_select_all() {
        readFootprintAdapter.selectAll();
    }

    @OnClick(R.id.bookbrack_delete_all_delete)
    void delete_confirm() {
        IosTypeDialog.Builder builder = new IosTypeDialog.Builder(this);
        builder.setContext("确定要删除收藏的全部书籍？");
        builder.setTitle("提示");
        builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                iosTypeDialog.dismiss();
                iosTypeDialog = null;

                List<ReadTrack> readTracks = readFootprintAdapter.deleteSelect();
                if (readTracks.size() < 1) {
                    no_book_show.setVisibility(View.VISIBLE);
                }

                Animation animBottomOut = AnimationUtils.loadAnimation(ReadFootprintActivity.this, R.anim.bottom_out);
                animBottomOut.setDuration(500);
                bookbrack_delete_all_line.setVisibility(View.GONE);
                bookbrack_delete_all_line.startAnimation(animBottomOut);
            }
        });
        builder.setRightButton("取消", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                iosTypeDialog.dismiss();
                iosTypeDialog = null;
            }
        });

        iosTypeDialog = builder.create();

        iosTypeDialog.show();
        setDialogWindowAttr(iosTypeDialog);
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        InvenoAdServiceHolder.getService().requestInfoAd(READ_FOOT_TRACE, ReadFootprintActivity.this)
                .onSuccess(new Function1<IndexedAdValueWrapper, Unit>() {
                    @Override
                    public Unit invoke(IndexedAdValueWrapper wrapper) {
//                        Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                        adBookModel = new AdReadTrackModel(wrapper);
                        adIndex = adBookModel.getIndex();
                        readFootprintAdapter.addAd(adIndex, adBookModel);
                        adCount++;
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
                        adCount--;
                        return null;
                    }
                }).execute();
    }

    private void loadMoreAd() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) footprint_recycle.getLayoutManager();
        if (layoutManager != null) {
            final int topSize = adCount + 10 * adCount;
            if (layoutManager.findLastVisibleItemPosition() >= (topSize - 2) && readFootprintAdapter.getData().size() >= (topSize + adIndex)) {
                adCount++;
                InvenoAdServiceHolder.getService().requestInfoAd(READ_FOOT_TRACE, ReadFootprintActivity.this)
                        .onSuccess(new Function1<IndexedAdValueWrapper, Unit>() {
                            @Override
                            public Unit invoke(IndexedAdValueWrapper wrapper) {
//                        Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                                AdReadTrackModel adBookModelMore = new AdReadTrackModel(wrapper);
                                if (adBookModel == null) {
                                    adBookModel = adBookModelMore;
                                }
                                int index = topSize + adBookModelMore.getIndex();
                                readFootprintAdapter.addAd(index, adBookModelMore);
                                return null;
                            }
                        }).onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
//                Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
                        adCount--;
                        return null;
                    }
                }).execute();
            }
        }
    }

    private void report() {
        ReportManager.INSTANCE.reportPageImp(9, "", this, ServiceContext.userService().getUserPid());
    }

    private void clickReport(long contentId) {
        ReportManager.INSTANCE.reportBookClick(9, "", "", 10,
                0, contentId, ReadFootprintActivity.this, ServiceContext.userService().getUserPid());
    }

    private void impReport(int first, int last) {
        List<ReadTrack> mBookselfs = new ArrayList<>(readFootprintAdapter.getData());
        int size = mBookselfs.size();
        int newFirst = first;
        int newLast = last;
//        Log.i("ReportManager", "size:" + size + " first:" + first + "  last:" + last + " newLast:" + newLast+ " newFirst:" + newFirst);
        if (newFirst >= 0 && newLast >= 0 && size > newLast) {
            for (int i = newFirst; i <= newLast; i++) {
                ReadTrack readTrack = mBookselfs.get(i);
                if (!(readTrack instanceof AdReadTrackModel)) {
//                    Log.i("ReportManager", "name:" + bookbrack.getBook_name() + " i:"+i);
                    long contentId = readTrack.getContent_id();
                    ReportManager.INSTANCE.reportBookImp(9, "", "", 10,
                            0, contentId, ReadFootprintActivity.this, ServiceContext.userService().getUserPid());
                }
            }
        }
    }

    private void impReport() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) footprint_recycle.getLayoutManager();
        if (layoutManager != null) {
            impReport(layoutManager.findFirstCompletelyVisibleItemPosition(), layoutManager.findLastVisibleItemPosition());
        }
    }
}
