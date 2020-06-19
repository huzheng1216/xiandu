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

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
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
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.BookCityAdapter;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;
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

/**
 * Created By huzheng
 * Date 2020/6/5
 * Des
 */
public class StoreItemFragment extends BaseFragment {

    private int channel;
    private RecyclerView recyclerView;
    private AdapterChannel adapterChannel;
    private BookCityAdapter bookCityAdapter;

    private ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();

    public StoreItemFragment(String title) {
        switch (title) {
            case "推荐":
                channel = 0;
                break;
            case "男频":
                channel = 1;
                break;
            case "女频":
                channel = 2;
                break;
            case "出版":
                channel = 3;
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
            APIContext.getBookCityAPi().getBookCity(channel)
                    .onSuccess(new Function1<ArrayList<BaseDataBean>, Unit>() {
                        @Override
                        public Unit invoke(ArrayList<BaseDataBean> baseDataBeans) {
                            mDataBeans = baseDataBeans;
                            bookCityAdapter.setDataList(mDataBeans);
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

    private void initData() {
        DDManager.getInstance().getRecommendList(channel, 50, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRequest<ResponseChannel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRequest<ResponseChannel> listBaseRequest) {
                        if (listBaseRequest.getData() != null && listBaseRequest.getData().getNovel_list() != null) {
                            adapterChannel.add(listBaseRequest.getData().getNovel_list());
                        } else {
                            Toaster.showToastCenter(getContext(), "获取数据失败:" + listBaseRequest.getCode());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toaster.showToastCenter(getContext(), "获取数据失败:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
//
//    @OnClick(R.id.classify)
//    void startClassify() {
//        Toaster.showToast(getActivity(), "分类");
//        ARouter.getInstance().build(ARouterPath.ACTIVITY_CLASSIFY).navigation();
//    }
//
//    @OnClick(R.id.rankiing)
//    void startRanking() {
//        Toaster.showToast(getActivity(), "排行榜");
//        ARouter.getInstance().build(ARouterPath.ACTIVITY_RANKING).navigation();
//    }
//
//    @OnClick(R.id.the_end)
//    void startTheEnd() {
//        Toaster.showToast(getActivity(), "完结");
//
//    }
}
