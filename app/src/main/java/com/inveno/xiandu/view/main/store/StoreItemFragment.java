package com.inveno.xiandu.view.main.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.alibaba.fastjson.JSON;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.bean.book.EditorRecommendList;
import com.inveno.xiandu.bean.book.RecommendName;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.bean.store.BannerDataBean;
import com.inveno.xiandu.bean.store.BannerDataList;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Const;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.api.book.GetBookCityAPi;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.BookCityAdapter;
import com.inveno.xiandu.view.adapter.BookCityBannerAdapter;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;
import com.inveno.xiandu.view.browser.BrowserActivity;
import com.inveno.xiandu.view.custom.IndicatorView;
import com.inveno.xiandu.view.custom.MRecycleScrollListener;
import com.inveno.xiandu.view.custom.MSwipeRefreshLayout;
import com.inveno.xiandu.view.main.AdapterChannel;
import com.inveno.xiandu.view.main.MainActivity;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.indicator.RoundLinesIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.GUESS_YOU_LIKE;

/**
 * Created By huzheng
 * Date 2020/6/5
 * Des
 */
public class StoreItemFragment extends BaseFragment {

    private int channel;
    private RecyclerView recyclerView;
    private BookCityAdapter bookCityAdapter;
    private Banner store_banner;
    private BookCityBannerAdapter bookCityBannerAdapter;
    private MSwipeRefreshLayout store_refresh_layout;
    private LinearLayout store_error;
    private TextView store_error_refresh;

    //显示用的banner数据列表
    private List<BannerDataBean> showBannerList = new ArrayList<>();

    private ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();
    private String topTitle;
    private String bottomTitle;

    private StatefulCallBack<BookShelfList> topRequest;
    private StatefulCallBack<BookShelfList> bottomRequest;

    private boolean isVisible;
    private int pageId = 2;

    public StoreItemFragment() {

    }

    public StoreItemFragment(String title) {
        switch (title) {
            case "推荐":
                channel = 0;
                topTitle = "小编推荐";
                bottomTitle = "猜你喜欢";
                pageId = 2;
                break;
            case "男频":
                channel = 1;
                topTitle = "男生热文";
                bottomTitle = "人气精选";
                pageId = 3;
                break;
            case "女频":
                channel = 2;
                topTitle = "女生热文";
                bottomTitle = "人气精选";
                pageId = 4;
                break;
            case "出版":
                channel = 3;
                topTitle = "精选畅销";
                bottomTitle = "人气精选";
                pageId = 0;
                break;
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_store_item, container, false);
        ButterKnife.bind(this, view);

        store_error = view.findViewById(R.id.store_error);
        store_error_refresh = view.findViewById(R.id.store_error_refresh);

        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        bookCityAdapter = new BookCityAdapter(getContext(), getActivity(), mDataBeans);
        bookCityAdapter.setHeaderView(initHeaderView());

        recyclerView.setAdapter(bookCityAdapter);
        bookCityAdapter.setOnItemClickListener(new BookCityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean, int position) {
                if (baseDataBean instanceof BookShelf) {
                    BookShelf bookShelf = (BookShelf) baseDataBean;
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                            .withString("json", GsonUtil.objectToJson(bookShelf))
                            .navigation();
                    clickReport(bookShelf.getContent_id(), position);
                } else if (baseDataBean instanceof EditorRecommend) {
                    //小编推荐需要去请求书本数据
                    EditorRecommend editorRecommend = (EditorRecommend) baseDataBean;
                    getBookToDetail(editorRecommend.getContent_id());
                    clickRecommandReport(editorRecommend.getContent_id(), position);

                }
            }

            @Override
            public void onChangeClick() {
                APIContext.getBookCityAPi().getBookCityTop(channel, getContext())
                        .onSuccess(new Function1<ArrayList<BaseDataBean>, Unit>() {
                            @Override
                            public Unit invoke(ArrayList<BaseDataBean> baseDataBeans) {
                                for (int i = 0; i < mDataBeans.size(); i++) {
                                    if (mDataBeans.get(i) instanceof RecommendName) {
                                        RecommendName recommendName = (RecommendName) mDataBeans.get(i);
                                        if (recommendName.getRecommendName().equals("人气精选")) {
                                            if (i > 0) {
                                                mDataBeans.subList(0, i).clear();
                                            }
                                            break;
                                        }
                                    }
                                }
                                baseDataBeans.addAll(mDataBeans);
                                mDataBeans = baseDataBeans;
                                bookCityAdapter.setDataList(mDataBeans);
//                                impReport();
                                return null;
                            }
                        })
                        .onFail(new Function2<Integer, String, Unit>() {
                            @Override
                            public Unit invoke(Integer integer, String s) {
                                Toaster.showToastShort(getActivity(), "没有书了");
                                return null;
                            }
                        }).execute();
            }
        });
        store_refresh_layout = view.findViewById(R.id.store_refresh_layout);
        store_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBannerData();
                getData();
            }
        });
        recyclerView.addOnScrollListener(new MRecycleScrollListener() {
            @Override
            public void onLoadMore() {
                bookCityAdapter.setFooterText("正在努力加载...");
                final List<BaseDataBean>[] mMoreData = new List[1];
                final boolean[] flag = new boolean[1];
                AdModel adModel = new AdModel();

                bottomRequest.onSuccess(new Function1<BookShelfList, Unit>() {
                    @Override
                    public Unit invoke(BookShelfList bookShelfList) {
                        mMoreData[0] = new ArrayList<>(bookShelfList.getNovel_list());
                        if (mMoreData[0].size() < 1) {
                            bookCityAdapter.setFooterText("没有更多数据");
                        }
                        if (flag[0]) {
                            if (adModel != null && adModel.getWrapper() != null) {
                                int adIndex = adModel.getWrapper().getIndex();
                                if (mMoreData[0].size() >= adIndex) {
                                    mMoreData[0].add(adIndex, adModel);
                                }
                            }
                            mDataBeans.addAll(mMoreData[0]);
                            bookCityAdapter.setDataList(mDataBeans);
                        }
                        return null;
                    }
                }).onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        bookCityAdapter.setFooterText("没有更多数据");
                        Toaster.showToastShort(getActivity(), "没有书了");
                        return null;
                    }
                }).execute();
                InvenoAdServiceHolder.getService().requestInfoAd(GUESS_YOU_LIKE, getContext()).onSuccess(wrapper -> {
                    Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                    adModel.setWrapper(wrapper);
                    if (mMoreData[0] != null && mMoreData[0].size() >= wrapper.getIndex()) {
                        mMoreData[0].add(wrapper.getIndex(), adModel);
                        mDataBeans.addAll(mMoreData[0]);
                        bookCityAdapter.setDataList(mDataBeans);
                    }
                    flag[0] = true;
                    return null;
                }).onFail((integer, s) -> {
                    Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
                    flag[0] = true;
                    return null;
                }).execute();
            }

            @Override
            public void onVisibleItem(int first, int last) {
                impReport(first, last);
            }
        });
        //TODO 这里可能内存泄漏
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                impReport();
            }
        });
        return view;
    }

    private View initHeaderView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_store_header, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView classify = view.findViewById(R.id.classify);
        classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_CLASSIFY).navigation();
            }
        });
        TextView rankiing = view.findViewById(R.id.rankiing);
        rankiing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_RANKING).navigation();
            }
        });
        TextView the_end = view.findViewById(R.id.the_end);
        the_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_RANKING).withBoolean("isEndRanking", true).navigation();
            }
        });
        store_banner = view.findViewById(R.id.store_banner);
        store_banner.addBannerLifecycleObserver(getActivity());
        store_banner.setIndicator(new RectangleIndicator(getContext()));

        bookCityBannerAdapter = new BookCityBannerAdapter(getActivity(), showBannerList);
        bookCityBannerAdapter.setBannerClickListener(new BookCityBannerAdapter.OnBannerClickListener() {
            @Override
            public void onItemClick(int position) {
                if (showBannerList.size() > position) {
                    BannerDataBean bannerDataBean = showBannerList.get(position);
                    //1书籍详情页 2 H5链接
                    if (bannerDataBean.getBanner_type() == 1) {
                        getBookToDetail(bannerDataBean.getBanner_book_id());
                    } else if (showBannerList.get(position).getBanner_type() == 2) {
                        // TODO: 2020/7/16 使用地址，跳到内部浏览器页
                        Intent intent = new Intent(getContext(), BrowserActivity.class);
                        intent.putExtra("browser_url", bannerDataBean.getBanner_web_url());
                        startActivity(intent);
                    }
                }
            }
        });
        store_banner.setAdapter(bookCityBannerAdapter);
        store_banner.start();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        //第一次加载数据
        Log.i("wyjjjjjjj", "页面: " + channel);
        Log.i("wyjjjjjjj", "是否第一次: " + firstVisble);
        isVisible = true;
        if (((StoreFragment) getParentFragment()).isVisible) {
            if (!((MainActivity) getActivity()).isOnPause()) {
                report();
            }
        }
        impReport();
        if (firstVisble) {
//            initData();
            getData();
            initLoadData();
        }
        //每次切换，都要检查一下数据
        getBannerData();
    }

    private void initLoadData() {
        if (channel == 1) {
            //男频
            //请求男生热文
            topRequest = APIContext.getBookCityAPi().getRecommend(1, 1, 4);
            //请求男生人气精选
            bottomRequest = APIContext.getBookCityAPi().getRecommend(1, 3, GetBookCityAPi.GET_DATA_PAGE_NUM);
        } else if (channel == 2) {
            //女频
            //请求女生热文
            topRequest = APIContext.getBookCityAPi().getRecommend(2, 2, 4);
            //请求女生人气精选
            bottomRequest = APIContext.getBookCityAPi().getRecommend(2, 5, GetBookCityAPi.GET_DATA_PAGE_NUM);
        } else if (channel == 3) {
            //出版畅销
            bottomRequest = APIContext.getBookCityAPi().getRecommend(3, 6, 4);
            //请求出版人气精选
            bottomRequest = APIContext.getBookCityAPi().getRecommend(3, 7, GetBookCityAPi.GET_DATA_PAGE_NUM);
        } else {
            topRequest = null;
            //推荐
            //请求猜你喜欢
            bottomRequest = APIContext.getBookCityAPi().getRecommend(0, 4, GetBookCityAPi.GET_DATA_PAGE_NUM);
        }
    }

    private void getBookToDetail(long content_id) {
        Toaster.showToastCenterShort(getContext(), "正在准备书籍，请稍后");
        APIContext.getBookCityAPi().getBook(content_id)
                .onSuccess(new Function1<BookShelf, Unit>() {
                    @Override
                    public Unit invoke(BookShelf bookShelf) {
                        ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                                .withString("json", GsonUtil.objectToJson(bookShelf))
                                .navigation();
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        Toaster.showToastCenterShort(getContext(), "获取数据失败");
                        return null;
                    }
                }).execute();
    }

    private void getData() {
        APIContext.getBookCityAPi().getBookCity(channel, getContext())
                .onSuccess(new Function1<ArrayList<BaseDataBean>, Unit>() {
                    @Override
                    public Unit invoke(ArrayList<BaseDataBean> baseDataBeans) {
                        store_refresh_layout.setRefreshing(false);
                        store_error.setVisibility(View.GONE);
                        mDataBeans = baseDataBeans;
                        if (mDataBeans.size() > 0) {
                            if (mDataBeans.size() < 10) {
                                bookCityAdapter.setFooterText("没有更多数据");
                            }
                            bookCityAdapter.setDataList(mDataBeans);
//                            impReport();
                        } else {
                            bookCityAdapter.setDataList(mDataBeans);
                            store_error.setVisibility(View.VISIBLE);
                            Toaster.showToastCenter(getContext(), "获取数据失败");
                            bookCityAdapter.setFooterText("没有更多数据");
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        store_refresh_layout.setRefreshing(false);
                        if (mDataBeans.size() < 1) {
                            bookCityAdapter.setDataList(mDataBeans);
                            store_error.setVisibility(View.VISIBLE);
                        } else {
                            store_error.setVisibility(View.GONE);
                        }
                        bookCityAdapter.setFooterText("没有更多数据");
                        Toaster.showToastCenter(getContext(), "获取数据失败");
                        return null;
                    }
                }).execute();
    }

    /**
     * 获取banner列表数据
     */
    private void getBannerData() {
        APIContext.getBookCityAPi().getBannerData(channel)
                .onSuccess(new Function1<BannerDataList, Unit>() {
                    @Override
                    public Unit invoke(BannerDataList bannerDataList) {
                        if (bannerDataList.getBanner_list().size() > 0) {
                            showBannerList.clear();
                            for (BannerDataBean bannerDataBean : bannerDataList.getBanner_list()) {
                                long sysTime = System.currentTimeMillis();
                                //在有效时间内才会显示
                                if (bannerDataBean.getStart_time() < sysTime && bannerDataBean.getEnd_time() > sysTime) {
                                    showBannerList.add(bannerDataBean);
                                }
                            }
                            if (showBannerList.size() > 0) {
                                store_banner.setVisibility(View.VISIBLE);
                                bookCityBannerAdapter.setmDatas(showBannerList);
                            } else {
                                store_banner.setVisibility(View.GONE);
                            }
                        } else {
                            store_banner.setVisibility(View.GONE);
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        if (showBannerList.size() < 1) {
                            store_banner.setVisibility(View.GONE);
                        }
                        return null;
                    }
                }).execute();
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        isVisible = false;
    }

    @OnClick(R.id.store_error_refresh)
    public void clickRefresh(View view) {
//            initData();
        getData();
        initLoadData();
    }

    //**************** 上报  start ******************//
    private void report() {
        ReportManager.INSTANCE.reportPageImp(pageId, "", getContext(), ServiceContext.userService().getUserPid());

    }

    public void checkAndReport() {
        if (isVisible) {
            report();
            impReport();
        }
    }

    private void clickRecommandReport(long contentId, int position) {
        ReportManager.INSTANCE.reportBookClick(pageId, "", "", 8, 0,
                contentId, getContext(), ServiceContext.userService().getUserPid());
    }

    private void clickReport(long contentId, int position) {
        ReportManager.INSTANCE.reportBookClick(pageId, "", "", getCurrntType(position),
                0, contentId, getContext(), ServiceContext.userService().getUserPid());
    }

    private void impReport(int first, int last) {
        int size = mDataBeans.size();
        if (size > last - 1) {
            for (int i = first - 1; i <= last - 1; i++) {
                if (i > 0) {
                    BaseDataBean baseDataBean = mDataBeans.get(i);
                    if (baseDataBean instanceof BookShelf) {
                        long contentId = ((BookShelf) baseDataBean).getContent_id();
                        ReportManager.INSTANCE.reportBookImp(pageId, "", "", getCurrntType(i),
                                0, contentId, getContext(), ServiceContext.userService().getUserPid());
                    } else if (baseDataBean instanceof EditorRecommend) {
                        long contentId = ((EditorRecommend) baseDataBean).getContent_id();
                        ReportManager.INSTANCE.reportBookImp(pageId, "", "", getCurrntType(i),
                                0, contentId, getContext(), ServiceContext.userService().getUserPid());
                    }
                }
            }
        }
    }

    private void impReport() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            impReport(layoutManager.findFirstCompletelyVisibleItemPosition(), layoutManager.findLastCompletelyVisibleItemPosition());
        }
    }

    private int getCurrntType(int position) {
        //TODO 暂时这么写
        int type = 8;
        if (position < bookCityAdapter.getCenterPostion()) {
            if (channel == 1) {
                type = 1;
            } else if (channel == 2) {
                type = 2;
            }
        } else {
            type = 4;
            if (channel == 1) {
                type = 3;
            } else if (channel == 2) {
                type = 5;
            }
        }
        return type;
    }
    //**************** 上报  end ******************//
}
