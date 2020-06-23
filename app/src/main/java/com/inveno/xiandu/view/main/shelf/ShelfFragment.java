package com.inveno.xiandu.view.main.shelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.response.ResponseShelf;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.GridSpacingItemDecoration;
import com.inveno.xiandu.view.components.PopupWindowShelfItem;
import com.inveno.xiandu.view.components.ShelfItemDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 书架界面-书架
 */
public class ShelfFragment extends BaseFragment {

    //基础控件
    private RecyclerView recyclerView;
    private ShelfAdapter shelfAdapter;

    public ShelfFragment() {
        super();
//        this.bookShelf = bookShelf;
    }

    @Override
    public ViewGroup initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.fragment_bookrack_main_shelf, null);
        recyclerView = inflate.findViewById(R.id.recycleview_fragment_bookrack_rack);

        shelfAdapter = new ShelfAdapter(getContext());
        shelfAdapter.setShelfAdapterListener(new ShelfAdapter.ShelfAdapterListener() {

            @Override
            public void onBookReadContinue(Bookbrack Bookbrack) {

            }

            @Override
            public void onBookDelete(Bookbrack Bookbrack) {

            }

            @Override
            public void onBookClick(Bookbrack bookShelf) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                        .withString("json", GsonUtil.objectToJson(bookShelf))
                        .navigation();
            }

            @Override
            public void onBookLongClick(Bookbrack bookShelf, View parent) {
            }

            @Override
            public void onFooterClick() {

            }
        });
        // 设置adapter
        recyclerView.setAdapter(shelfAdapter);
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);  // 垂直排列
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, getResources().getDimensionPixelSize(R.dimen.adapter_search_history), true));
        recyclerView.setLayoutManager(linearLayoutManager);
        return inflate;
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        LogUtils.H("书架可见：" + firstVisble);
        initData();
        //加载网络数据
//        DDManager.getInstance().getBookShelf()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseRequest<ResponseShelf>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(BaseRequest<ResponseShelf> listBaseRequest) {
//                        if (listBaseRequest != null && listBaseRequest.getData().getBook_list() != null) {
//                            //同步数据
//                            syncData(listBaseRequest.getData().getBook_list());
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtils.H("hhhhh");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    /**
     * 对比书架数据
     *
     * @param data
     */
    private void syncData(List<Bookbrack> data) {
//        //简单判断一下
//        if (SQL.getInstance().getAllBookShelf().size() != data.size()) {
//            SQL.getInstance().insertOrReplace(data);
//            initData();
//        }
        if (SQL.getInstance().getAllBookbrack().size() != data.size()) {
            SQL.getInstance().insertOrReplaceBookbrack(data);
            initData();
        }
    }

    private void initData() {
//        data.addAll(SQL.getInstance(getContext()).getAllBookByShelfId(bookShelf.getId()));
        shelfAdapter.setData(SQL.getInstance().getAllBookbrack());

//        shelfAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onMoveTop(int position) {
////        BookShelf remove = data.remove(position);
////        data.add(0, remove);
//        shelfAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onMoveYanFei(int position) {
////        BookShelf remove = data.remove(position);
//        shelfAdapter.notifyDataSetChanged();
////        remove.setShelfId("yf");
//    }
//
//    @Override
//    public void onDownload(int position) {
//        Toaster.showToastCenter(getContext(), "下载：" + position);
//    }
//
//    @Override
//    public void onDel(int position) {
////        BookShelf remove = data.remove(position);
//        shelfAdapter.notifyDataSetChanged();
//    }
}
