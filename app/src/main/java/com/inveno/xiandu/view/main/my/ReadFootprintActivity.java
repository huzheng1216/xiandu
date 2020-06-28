package com.inveno.xiandu.view.main.my;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdBookModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
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
import com.inveno.xiandu.view.main.MainActivity;
import com.inveno.xiandu.view.main.shelf.ShelfAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.RANKING_LIST;
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

    private AdBookModel adBookModel;


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
    }

    @Override
    protected void initView() {
        super.initView();
        RecyclerView footprint_recycle = findViewById(R.id.footprint_recycle);
        footprint_recycle.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        readFootprintAdapter = new ReadFootprintAdapter(this);

        no_book_show = findViewById(R.id.no_book_show);
        // 设置adapter
        footprint_recycle.setAdapter(readFootprintAdapter);
        // 设置Item添加和移除的动画
        footprint_recycle.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        footprint_recycle.setLayoutManager(dataLayoutManager);

        readFootprintAdapter.setShelfAdapterListener(new ReadFootprintAdapter.ShelfAdapterListener() {

            @Override
            public void onBookReadContinue(Bookbrack Bookbrack) {
            }

            @Override
            public void onBookDelete(Bookbrack Bookbrack) {
                IosTypeDialog.Builder builder = new IosTypeDialog.Builder(ReadFootprintActivity.this);
                builder.setContext("确定要删除这本书？");
                builder.setTitle("提示");
                builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog = null;

                        List<Bookbrack> bookbracks = readFootprintAdapter.deleteSelect();
                        if (bookbracks.size() < 1) {
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
            public void onBookClick(Bookbrack bookbrack) {
                // TODO: 2020/6/17  去数据库查书，查到了就跳转，没查到就请求后跳转
                BookShelf bookShelf = SQL.getInstance().getBookShelf(bookbrack.getContent_id());
                if (bookShelf != null) {
                    //这里需要跳转到小说阅读
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                            .withString("json", GsonUtil.objectToJson(bookShelf))
                            .withInt("capter", bookbrack.getChapter_id())
                            .navigation();
                } else {
                    APIContext.getBookCityAPi().getBook(bookbrack.getContent_id())
                            .onSuccess(new Function1<BookShelf, Unit>() {
                                @Override
                                public Unit invoke(BookShelf bookShelf) {
                                    ARouter.getInstance().build(ARouterPath.ACTIVITY_CONTENT_MAIN)
                                            .withString("json", GsonUtil.objectToJson(bookShelf))
                                            .withInt("capter", bookbrack.getChapter_id())
                                            .navigation();
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
            }

            @Override
            public void onBookLongClick(Bookbrack bookbrack, View parent) {
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
                APIContext.bookbrackApi().getBookbrackList(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid())
                        .onSuccess(new Function1<List<Bookbrack>, Unit>() {
                            @Override
                            public Unit invoke(List<Bookbrack> bookbracks) {
                                swipeRefreshLayout.setRefreshing(false);
//                            //同步数据
                                syncData(bookbracks);
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
        APIContext.bookbrackApi().getBookbrackList(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid())
                .onSuccess(new Function1<List<Bookbrack>, Unit>() {
                    @Override
                    public Unit invoke(List<Bookbrack> bookbracks) {
//                            //同步数据
                        syncData(bookbracks);
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
    private void syncData(List<Bookbrack> data) {
//        SQL.getInstance().insertOrReplaceBookbrack(data);
        initData();
//        //简单判断一下
        if (SQL.getInstance().getAllBookShelf().size() != data.size()) {
            SQL.getInstance().insertOrReplaceBookbrack(data);
            initData();
        }
    }

    private void initData() {
        List<Bookbrack> bookbracks = SQL.getInstance().getAllBookbrack();
        if (bookbracks.size() < 1) {
            no_book_show.setVisibility(View.VISIBLE);
        } else {
            no_book_show.setVisibility(View.GONE);
        }
        //TODO 加广告
        if (adBookModel != null) {
            int index = adBookModel.getIndex();
            if (bookbracks.size() >= index) {
                bookbracks.add(index, adBookModel);
            }
        }
        readFootprintAdapter.setData(bookbracks);
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

                List<Bookbrack> bookbracks = readFootprintAdapter.deleteSelect();
                if (bookbracks.size() < 1) {
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
                        Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                        adBookModel = new AdBookModel(wrapper);
                        readFootprintAdapter.addAd(adBookModel);
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
                        return null;
                    }
                }).execute();
    }
}
