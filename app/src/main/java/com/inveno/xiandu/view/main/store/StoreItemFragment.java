package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
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
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.response.ResponseChannel;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Const;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.main.AdapterChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By huzheng
 * Date 2020/6/5
 * Des
 */
public class StoreItemFragment extends BaseFragment {

    int channel;
    RecyclerView recyclerView;
    AdapterChannel adapterChannel;

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
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterChannel = new AdapterChannel(getContext());
        recyclerView.setAdapter(adapterChannel);
        adapterChannel.setOnItemClickListener(new AdapterChannel.OnItemClickListener() {
            @Override
            public void onItemClick(BookShelf bookShelf) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                        .withString("json", GsonUtil.objectToJson(bookShelf))
                        .navigation();
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
        if (firstVisble) {
            initData();
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
}
