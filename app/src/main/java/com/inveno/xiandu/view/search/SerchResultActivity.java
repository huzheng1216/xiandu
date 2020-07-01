package com.inveno.xiandu.view.search;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.adapter.BookCityAdapter;
import com.inveno.xiandu.view.adapter.SearchDataAdapter;
import com.inveno.xiandu.view.custom.MRecycleScrollListener;
import com.inveno.xiandu.view.detail.DetailActivity;
import com.inveno.xiandu.view.components.HeaderBar;
import com.inveno.xiandu.view.components.PullRecyclerViewGroup2;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 搜索
 */
@Route(path = ARouterPath.ACTIVITY_SEARCH_RESULT)
public class SerchResultActivity extends BaseActivity {

    @Autowired(name = "name")
    protected String name;
    //    private SearchResultAdapter searchAdapter;
    private SearchDataAdapter searchDataAdapter;
    private List<BookShelf> data = new ArrayList<>();
    private ArrayList<BaseDataBean> mData = new ArrayList<>();

    @BindView(R.id.headerBar_search_result)
    HeaderBar headerBar;
    @BindView(R.id.recyclerView)
    RecyclerView resultRecyclerView;
    @BindView(R.id.loading_view)
    View loadingView;
    @BindView(R.id.iv_no_data)
    View noDataView;
    @BindView(R.id.no_book_show)
    TextView no_book_show;
//    @BindView(R.id.recyclerView_layout)
//    PullRecyclerViewGroup2 pullRecyclerViewGroup2;

    //搜索任务
    private Subscription subscription;
    private int page_num = 1;//搜索的页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        setStatusBar(R.color.white, true);

        no_book_show.setVisibility(View.GONE);
        //头部
        headerBar.setTitle(name).showBackImg().setListener(new HeaderBar.OnActionListener() {
            @Override
            public void onAction(int action, Object object) {
                if (action == HeaderBar.BACK) {
                    finish();
                }
            }
        });
//        pullRecyclerViewGroup2.isTopVisible = true;
        //数据
        searchDataAdapter = new SearchDataAdapter(this, this, mData);
        resultRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        resultRecyclerView.addItemDecoration(new DividerItemDecorationStyleOne(this, DividerItemDecorationStyleOne.VERTICAL_LIST, R.drawable.divider_recycleview, 0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(linearLayoutManager);
        resultRecyclerView.setAdapter(searchDataAdapter);
        searchDataAdapter.setOnItemClickListener(new SearchDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean) {
                if (baseDataBean instanceof BookShelf) {
                    Toaster.showToastCenterShort(SerchResultActivity.this, "正在准备书籍，请稍后");
                    //请求完整图书数据后跳转
                    APIContext.getBookCityAPi().getBook(((BookShelf) baseDataBean).getContent_id())
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
                                    return null;
                                }
                            }).execute();
                }
            }
        });
        resultRecyclerView.addOnScrollListener(new MRecycleScrollListener() {
            @Override
            public void onLoadMore() {
//                Toaster.showToastShort(SerchResultActivity.this, "上拉加载");
                if (!searchDataAdapter.isNotMore()) {
                    page_num++;
                    startBookSearch();
                }
            }

            @Override
            public void onVisibleItem(int first, int last) {

            }
        });

        startBookSearch();
        //执行搜索任务
//        SearchTool.getInstance().search(name).subscribe(new Subscriber<List<Book>>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                headerBar.showProgress();
//                loadingView.setVisibility(View.VISIBLE);
//                subscription = s;
//                //让下游处理10个结果
//                s.request(10);
//            }
//
//            @Override
//            public void onNext(List<Book> books) {
//                L.H(books.size() + "条");
//                sortData(name, books);
//                if (data.size() > 0) {
//                    loadingView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                headerBar.hitenProgress();
//                loadingView.setVisibility(View.GONE);
//                if (data.size() <= 0) {
//                    noDataView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onComplete() {
//                headerBar.hitenProgress();
//                loadingView.setVisibility(View.GONE);
//                if (data.size() <= 0) {
//                    noDataView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    private void startBookSearch() {
        APIContext.SearchBookApi().searchBook(name, page_num)
                .onSuccess(new Function1<BookShelfList, Unit>() {
                    @Override
                    public Unit invoke(BookShelfList bookShelfList) {
                        if (bookShelfList.getNovel_list().size() > 0) {
                            ArrayList<BaseDataBean> baseDataBeans = new ArrayList<>(bookShelfList.getNovel_list());
                            mData.addAll(baseDataBeans);
                            searchDataAdapter.setDataList(mData);
                        } else {
                            if (mData.size() < 1) {
                                no_book_show.setVisibility(View.VISIBLE);
                            }
                            searchDataAdapter.setNotDataFooter();
                        }
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
    public void finish() {
        super.finish();
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
