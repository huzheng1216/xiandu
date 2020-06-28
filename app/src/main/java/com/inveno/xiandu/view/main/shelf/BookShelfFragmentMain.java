package com.inveno.xiandu.view.main.shelf;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdBookModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.coin.ReadTime;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.custom.MSwipeRefreshLayout;
import com.inveno.xiandu.view.custom.SwipeItemLayout;
import com.inveno.xiandu.view.dialog.IosTypeDialog;
import com.inveno.xiandu.view.main.MainActivity;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.RANKING_LIST;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 书架界面
 */
public class BookShelfFragmentMain extends BaseFragment implements View.OnClickListener {

    //基础控件
    private MSwipeRefreshLayout swipeRefreshLayout;
    private ShelfAdapter shelfAdapter;

    private LinearLayout bookbrack_delete_all_line;
    private TextView bookbrack_delete_all_cancel;
    private TextView bookbrack_delete_all_select_all;
    private TextView bookbrack_delete_all_delete;

    private IosTypeDialog iosTypeDialog;
    private AdBookModel adBookModel;

    public void SearchFragmentMain() {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.fragment_bookrack, null);

        bookbrack_delete_all_line = inflate.findViewById(R.id.bookbrack_delete_all_line);
        bookbrack_delete_all_cancel = inflate.findViewById(R.id.bookbrack_delete_all_cancel);
        bookbrack_delete_all_cancel.setOnClickListener(this);
        bookbrack_delete_all_select_all = inflate.findViewById(R.id.bookbrack_delete_all_select_all);
        bookbrack_delete_all_select_all.setOnClickListener(this);
        bookbrack_delete_all_delete = inflate.findViewById(R.id.bookbrack_delete_all_delete);
        bookbrack_delete_all_delete.setOnClickListener(this);

        swipeRefreshLayout = inflate.findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        RecyclerView bookrack_recyclerview = inflate.findViewById(R.id.bookrack_recyclerview);
        bookrack_recyclerview.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));

        shelfAdapter = new ShelfAdapter(getContext());
        shelfAdapter.setShelfAdapterListener(new ShelfAdapter.ShelfAdapterListener() {

            @Override
            public void onBookReadContinue(Bookbrack Bookbrack) {
            }

            @Override
            public void onBookDelete(Bookbrack Bookbrack) {
                IosTypeDialog.Builder builder = new IosTypeDialog.Builder(getActivity());
                builder.setContext("确定要删除这本书？");
                builder.setTitle("提示");
                builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog = null;

                        shelfAdapter.deleteSelect();
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
                //隐藏父类底部tab
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setBottomVisiable();
                }
                Animation animBottomIn = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.bottom_in);
                animBottomIn.setDuration(500);
                bookbrack_delete_all_line.setVisibility(View.VISIBLE);
                bookbrack_delete_all_line.startAnimation(animBottomIn);
                shelfAdapter.setSelect(true);
            }

            @Override
            public void onFooterClick() {
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setCheckViewPager(1);
                }
            }
        });
        // 设置adapter
        bookrack_recyclerview.setAdapter(shelfAdapter);
        // 设置Item添加和移除的动画
        bookrack_recyclerview.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(getActivity());
        bookrack_recyclerview.setLayoutManager(dataLayoutManager);
        initHeaderView();
        initFooterView();
        loadAd();
        return inflate;
    }

    private void initHeaderView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bookrack_header, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        shelfAdapter.setHeaderView(view);
    }

    private void initFooterView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_bookshelf_foot, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        shelfAdapter.setFooterView(view);
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        LogUtils.H("书架可见：" + firstVisble);
        initData();
        if (firstVisble) {
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
        //获取今日已读和今日金币
        if (ServiceContext.userService().isLogin()) {
            get_coin();
            get_read_time();
        } else {
            shelfAdapter.setCoinNum("--");
            shelfAdapter.setHeaderTime("--");
        }
        shelfAdapter.setData(SQL.getInstance().getAllBookbrack());
        List<Bookbrack> list = SQL.getInstance().getAllBookbrack();
        if (adBookModel != null) {
            int index = adBookModel.getIndex();
            if (list.size() >= index) {
                list.add(index, adBookModel);
            }
        }
        shelfAdapter.setData(list);
    }

    //今日金币
    private void get_coin() {
        APIContext.coinApi().queryCoin()
                .onSuccess(new Function1<UserCoinOut, Unit>() {
                    @Override
                    public Unit invoke(UserCoinOut userCoin) {
                        UserCoin mUserCoin = userCoin.getCoin();
                        shelfAdapter.setCoinNum(String.valueOf(mUserCoin.getCurrent_coin()));
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

    //今日已读
    public void get_read_time() {
        APIContext.coinApi().readTime()
                .onSuccess(new Function1<ReadTime, Unit>() {
                    @Override
                    public Unit invoke(ReadTime readTime) {
                        shelfAdapter.setHeaderTime(String.valueOf(readTime.getRead_time() / 60));
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bookbrack_delete_all_cancel) {

            Animation animBottomOut = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_out);
            animBottomOut.setDuration(500);
            bookbrack_delete_all_line.setVisibility(View.GONE);
            bookbrack_delete_all_line.startAnimation(animBottomOut);

            shelfAdapter.setSelect(false);
            //隐藏与显示父类底部tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setBottomVisiable();
            }
        } else if (id == R.id.bookbrack_delete_all_select_all) {
            shelfAdapter.selectAll();
        } else if (id == R.id.bookbrack_delete_all_delete) {
            IosTypeDialog.Builder builder = new IosTypeDialog.Builder(getActivity());
            builder.setContext("确定要删除收藏的全部书籍？");
            builder.setTitle("提示");
            builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosTypeDialog.dismiss();
                    iosTypeDialog = null;

                    shelfAdapter.deleteSelect();
                    //隐藏与显示父类底部tab
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setBottomVisiable();
                    }

                    Animation animBottomOut = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.bottom_out);
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
    }

    //在dialog.show()之后调用
    public void setDialogWindowAttr(Dialog dlg) {
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.8); //设置宽度
        dlg.getWindow().setAttributes(lp);
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        InvenoAdServiceHolder.getService().requestInfoAd(RANKING_LIST, getContext()).onSuccess(new Function1<IndexedAdValueWrapper, Unit>() {
            @Override
            public Unit invoke(IndexedAdValueWrapper wrapper) {
//                Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                adBookModel = new AdBookModel(wrapper);
                shelfAdapter.addAd(adBookModel);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
//                Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
                return null;
            }
        }).execute();
    }
}
