package com.inveno.xiandu.view.search;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.book.DetailActivity;
import com.inveno.xiandu.view.components.HeaderBar;
import com.inveno.xiandu.view.components.PullRecyclerViewGroup2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 搜索
 */
@Route(path = ARouterPath.ACTIVITY_SEARCH_RESULT)
public class SerchResultActivity extends BaseActivity {

    @Autowired(name = "name")
    protected String name;
    private SearchResultAdapter searchAdapter;
    private List<Book> data = new ArrayList<>();

    @BindView(R.id.headerBar_search_result)
    HeaderBar headerBar;
    @BindView(R.id.recyclerView)
    RecyclerView resultRecyclerView;
    @BindView(R.id.loading_view)
    View loadingView;
    @BindView(R.id.iv_no_data)
    View noDataView;
    @BindView(R.id.recyclerView_layout)
    PullRecyclerViewGroup2 pullRecyclerViewGroup2;

    //搜索任务
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        setStatusBar(R.color.white, true);

        //头部
        headerBar.setTitle(name).showBackImg().setListener(new HeaderBar.OnActionListener() {
            @Override
            public void onAction(int action, Object object) {
                if (action == HeaderBar.BACK) {
                    finish();
                }
            }
        });
        pullRecyclerViewGroup2.isTopVisible = true;
        //数据
        searchAdapter = new SearchResultAdapter(this, data, name);
        resultRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        resultRecyclerView.addItemDecoration(new DividerItemDecorationStyleOne(this, DividerItemDecorationStyleOne.VERTICAL_LIST, R.drawable.divider_recycleview, 0));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(linearLayoutManager);
        resultRecyclerView.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onClick(Book book, ImageView pic) {
                Intent intent = new Intent(SerchResultActivity.this, DetailActivity.class);
                intent.putExtra("json", GsonUtil.objectToJson(book));
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) SerchResultActivity.this, pic, "photo").toBundle();
                startActivity(intent, bundle);
            }
        });

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

    /**
     * 给搜索结果排序
     *
     * @param name
     * @param books
     */
    private void sortData(String name, List<Book> books) {
        if (books.size() > 0) {
            for (Book book : books) {
                if (book.getName().equals(name)) {
                    data.add(0, book);
                } else if (book.getName().contains(name)) {
                    data.add(book);
                } else if (book.getAuthor().contains(name)) {
                    data.add(book);
                }
            }
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
