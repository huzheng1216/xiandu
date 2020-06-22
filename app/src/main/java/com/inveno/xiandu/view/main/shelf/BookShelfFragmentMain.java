package com.inveno.xiandu.view.main.shelf;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.basics.service.app.info.AppInfo;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.response.ResponseShelf;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.invenohttp.service.UserService;
import com.inveno.xiandu.utils.DensityUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.GridSpacingItemDecoration;
import com.inveno.xiandu.view.components.PopupWindowShelfItem;
import com.inveno.xiandu.view.components.tablayout.MyTabLayout;
import com.inveno.xiandu.view.custom.MSwipeRefreshLayout;
import com.inveno.xiandu.view.custom.SwipeItemLayout;
import com.inveno.xiandu.view.main.MainActivity;

import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

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
                swipeRefreshLayout.setRefreshing(false);
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
        });
        RecyclerView bookrack_recyclerview = inflate.findViewById(R.id.bookrack_recyclerview);
        bookrack_recyclerview.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));

        shelfAdapter = new ShelfAdapter(getContext());
        shelfAdapter.setShelfAdapterListener(new ShelfAdapter.ShelfAdapterListener() {

            @Override
            public void onBookReadContinue(Bookbrack Bookbrack) {
                Toaster.showToast(getActivity(),"继续阅读");
            }

            @Override
            public void onBookClick(Bookbrack bookbrack) {
                // TODO: 2020/6/17  去数据库查书，查到了就跳转，没查到就请求后跳转
                BookShelf bookShelf = SQL.getInstance().getBookShelf(bookbrack.getContent_id());
                if (bookShelf != null) {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                            .withString("json", GsonUtil.objectToJson(bookShelf))
                            .navigation();
                } else {
                    APIContext.getBookCityAPi().getBook(bookbrack.getContent_id())
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
        });
        // 设置adapter
        bookrack_recyclerview.setAdapter(shelfAdapter);
        // 设置Item添加和移除的动画
        bookrack_recyclerview.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(getActivity());
        bookrack_recyclerview.setLayoutManager(dataLayoutManager);
        initHeaderView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shelfAdapter.setHeaderData("--", "--");
            }
        }, 8000);

        return inflate;
    }

    private void initHeaderView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bookrack_header, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        shelfAdapter.setHeaderView(view);
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        LogUtils.H("书架可见：" + firstVisble);
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
        shelfAdapter.setData(SQL.getInstance().getAllBookbrack());
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
    }
}
