package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.alibaba.fastjson.JSON;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.bean.book.EditorRecommendList;
import com.inveno.xiandu.bean.book.RecommendName;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Const;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.api.book.GetBookCityAPi;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.BookCityAdapter;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;
import com.inveno.xiandu.view.custom.MRecycleScrollListener;
import com.inveno.xiandu.view.custom.MSwipeRefreshLayout;
import com.inveno.xiandu.view.main.AdapterChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.BOY_GIRL_BOTTOM;
import static com.inveno.android.ad.config.ScenarioManifest.EDITOR_RECOMMEND;
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
    private MSwipeRefreshLayout store_refresh_layout;

    private ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();
    private ArrayList<BaseDataBean> mTopDataBeans = new ArrayList<>();
    private ArrayList<BaseDataBean> mBottomDataBeans = new ArrayList<>();
    private String topTitle;
    private String bottomTitle;

    private StatefulCallBack<BookShelfList> topRequest;
    private StatefulCallBack<BookShelfList> bottomRequest;

    public StoreItemFragment(String title) {
        switch (title) {
            case "推荐":
                channel = 0;
                topTitle = "小编推荐";
                bottomTitle = "猜你喜欢";
                break;
            case "男频":
                channel = 1;
                topTitle = "男生热文";
                bottomTitle = "人气精选";
                break;
            case "女频":
                channel = 2;
                topTitle = "女生热文";
                bottomTitle = "人气精选";
                break;
            case "出版":
                channel = 3;
                topTitle = "精选畅销";
                bottomTitle = "人气精选";
                break;
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_store_item, container, false);
        ButterKnife.bind(this, view);
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        bookCityAdapter = new BookCityAdapter(getContext(), getActivity(), mDataBeans);
        recyclerView.setAdapter(bookCityAdapter);
        bookCityAdapter.setOnItemClickListener(new BookCityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean) {
                if (baseDataBean instanceof BookShelf) {
                    BookShelf bookShelf = (BookShelf) baseDataBean;
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                            .withString("json", GsonUtil.objectToJson(bookShelf))
                            .navigation();
                } else if (baseDataBean instanceof EditorRecommend) {
                    //小编推荐需要去请求书本数据
                    EditorRecommend editorRecommend = (EditorRecommend) baseDataBean;
                    APIContext.getBookCityAPi().getBook(editorRecommend.getContent_id())
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
                                    Toaster.showToastCenter(getContext(), "获取数据失败:" + integer);
                                    return null;
                                }
                            }).execute();
                }
            }
        });
        store_refresh_layout = view.findViewById(R.id.store_refresh_layout);
        store_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        recyclerView.addOnScrollListener(new MRecycleScrollListener() {
            @Override
            public void onLoadMore() {

                final List<BaseDataBean>[] mMoreData = new List[1];
                final boolean[] flag = new boolean[1];
                AdModel adModel = new AdModel();

                bottomRequest.onSuccess(new Function1<BookShelfList, Unit>() {
                    @Override
                    public Unit invoke(BookShelfList bookShelfList) {
                        mMoreData[0] = new ArrayList<>(bookShelfList.getNovel_list());
                        if (flag[0] == true) {
                            int adIndex = adModel.getWrapper().getIndex();
                            if (mMoreData[0].size() >=  adIndex) {
                                mMoreData[0].add(adIndex, adModel);
                            }
                            mDataBeans.addAll(mMoreData[0]);
                            bookCityAdapter.setDataList(mDataBeans);
                        }
                        return null;
                    }
                }).onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        Toaster.showToastShort(getActivity(), "请求出错了：" + integer);
                        return null;
                    }
                }).execute();
                InvenoAdServiceHolder.getService().requestInfoAd(GUESS_YOU_LIKE, getContext()).onSuccess(wrapper -> {
                    Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
                    adModel.setWrapper(wrapper);
                    if (mMoreData[0]!=null && mMoreData[0].size() >= wrapper.getIndex()) {
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
        });
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
        if (firstVisble) {
//            initData();
            getData();
            initLoadData();

        }
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

    private StatefulCallBack<BookShelfList> getRecommendData(int channel_id, int type, int num, boolean isTop) {
        return APIContext.getBookCityAPi().getRecommend(channel_id, type, num);
    }

    private void getEditorRecommendData() {
        APIContext.getBookCityAPi().getEditorRecommend()
                .onSuccess(new Function1<EditorRecommendList, Unit>() {
                    @Override
                    public Unit invoke(EditorRecommendList editorRecommendList) {
                        RecommendName recommendName = new RecommendName();
                        recommendName.setRecommendName(topTitle);
                        recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                        mTopDataBeans.clear();
                        mTopDataBeans.add(recommendName);
                        mTopDataBeans.addAll(editorRecommendList.getNovel_list());
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

    private void getData() {
        APIContext.getBookCityAPi().getBookCity(channel, getContext())
                .onSuccess(new Function1<ArrayList<BaseDataBean>, Unit>() {
                    @Override
                    public Unit invoke(ArrayList<BaseDataBean> baseDataBeans) {
                        store_refresh_layout.setRefreshing(false);
                        mDataBeans = baseDataBeans;
                        bookCityAdapter.setDataList(mDataBeans);
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        store_refresh_layout.setRefreshing(false);
                        Toaster.showToastCenter(getContext(), "获取数据失败:" + integer);
                        return null;
                    }
                }).execute();
    }
}
