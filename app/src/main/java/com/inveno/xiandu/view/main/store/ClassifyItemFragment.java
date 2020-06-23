package com.inveno.xiandu.view.main.store;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ClassifyData;
import com.inveno.xiandu.bean.book.ClassifyMenu;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.LeftMenuAdapter;
import com.inveno.xiandu.view.adapter.RightDataAdapter;
import com.inveno.xiandu.view.custom.MRecycleScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/17 10:53
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ClassifyItemFragment extends BaseFragment {
    private int channel;

    private TextView book_sum;
    private TextView no_book_show;
    private RightDataAdapter rightDataAdapter;
    private LeftMenuAdapter leftMenuAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<ClassifyMenu> mMenus = new ArrayList<>();
    private List<BaseDataBean> mBookselfs = new ArrayList<>();

    private int knowClassifyPosition = 0;
    private int book_status = -1;

    private HashMap<Integer, ClassifyData> mClassifyDatas = new HashMap<>();

    public ClassifyItemFragment(String title) {
        switch (title) {
            case "男生":
                channel = 1;
                break;
            case "女生":
                channel = 2;
                break;
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_classify_item, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        book_sum = view.findViewById(R.id.book_sum);
        no_book_show = view.findViewById(R.id.no_book_show);
        book_sum.setText("共计0本");
        RecyclerView ranking_data_recycle = view.findViewById(R.id.ranking_data_recycle);
        RecyclerView ranking_menu_recycle = view.findViewById(R.id.ranking_menu_recycle);

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(getActivity());
        ranking_data_recycle.setLayoutManager(dataLayoutManager);
        rightDataAdapter = new RightDataAdapter(getActivity(), getActivity(), mBookselfs);
        rightDataAdapter.setFooterView(getViewHolderView(getContext(), R.layout.item_right_load_more));
        ranking_data_recycle.setAdapter(rightDataAdapter);
        rightDataAdapter.setOnitemClickListener(new RightDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean) {
                if (baseDataBean instanceof BookShelf) {
                    BookShelf bookShelf = (BookShelf) baseDataBean;
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                            .withString("json", GsonUtil.objectToJson(bookShelf))
                            .navigation();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        ranking_menu_recycle.setLayoutManager(linearLayoutManager);
        leftMenuAdapter = new LeftMenuAdapter(getActivity(), mMenus);
        ranking_menu_recycle.setAdapter(leftMenuAdapter);
        leftMenuAdapter.setOnitemClickListener(new LeftMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (knowClassifyPosition != position) {
                    //点击后列表初始化，准备填充新数据
                    knowClassifyPosition = position;
                    mBookselfs.clear();
                    rightDataAdapter.setmDataList(mBookselfs);
                    book_sum.setText("共计0本");
                    //先从缓存里面拿
                    ClassifyData classifyData = mClassifyDatas.get(mMenus.get(knowClassifyPosition).getCategory_id());

                    if (classifyData != null) {
                        //缓存有数据，直接填充
                        mBookselfs = new ArrayList<>(classifyData.getNovel_list());
                        rightDataAdapter.setmDataList(mBookselfs);
                        if (mBookselfs.size() < 1) {
                            no_book_show.setVisibility(View.VISIBLE);
                            book_sum.setText("");
                        } else {
                            no_book_show.setVisibility(View.GONE);
                        }
                    } else {
                        //缓存没数据，需要去请求
                        getClassifyData(1);
                    }
                }
            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新要清掉旧数据
                //点击后列表初始化，准备填充新数据
                mBookselfs.clear();
                rightDataAdapter.setmDataList(mBookselfs);
                book_sum.setText("共计0本");
                mClassifyDatas.remove(mMenus.get(knowClassifyPosition).getCategory_id());
                //刷新请求新数据
                getClassifyData(1);
            }
        });
        //上拉加载
        ranking_data_recycle.addOnScrollListener(new MRecycleScrollListener() {
            @Override
            public void onLoadMore() {
//                Toaster.showToastShort(SerchResultActivity.this, "上拉加载");
                if (!rightDataAdapter.isNotMore()) {
                    ClassifyData mClassifyData = mClassifyDatas.get(mMenus.get(knowClassifyPosition).getCategory_id());
                    if (mClassifyData != null) {
                        getClassifyData(mClassifyData.getPageNum());
                    } else {
                        getClassifyData(1);
                    }
                }
            }
        });
        return view;
    }

    private View getViewHolderView(Context context, int p) {
        View view = LayoutInflater.from(context).inflate(p, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        //第一次加载数据
        Log.i("ClassifyItemFragment", "页面: " + channel);
        Log.i("ClassifyItemFragment", "是否第一次: " + firstVisble);
        if (firstVisble) {
            //先加载分类菜单
            APIContext.getBookCityAPi().getClassifyMenu(channel)
                    .onSuccess(new Function1<List<ClassifyMenu>, Unit>() {
                        @Override
                        public Unit invoke(List<ClassifyMenu> classifyMenus) {
                            mMenus = classifyMenus;
                            leftMenuAdapter.setMenusData(mMenus);
                            knowClassifyPosition = 0;
                            getClassifyData(1);
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
     * 获取分类数据
     */
    private void getClassifyData(int page_num) {
        APIContext.getBookCityAPi().getClassifyData(mMenus.get(knowClassifyPosition).getCategory_id(), book_status, page_num)
                .onSuccess(new Function1<ClassifyData, Unit>() {
                    @Override
                    public Unit invoke(ClassifyData classifyData) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        // TODO: 2020/6/17 这里先缓存记录分类数据，后续更改使用数据库存储
                        ClassifyData mClassifyData = mClassifyDatas.get(classifyData.getCategory_id());
                        //是否又记录数据
                        if (mClassifyData == null) {
                            classifyData.setPageNum(2);
                            mClassifyDatas.put(classifyData.getCategory_id(), classifyData);
                        } else {
                            if (classifyData.getNovel_list().size() > 0) {
                                if (mClassifyData.getNovel_list().size() < 1) {
                                    classifyData.setPageNum(2);
                                    mClassifyDatas.put(classifyData.getCategory_id(), classifyData);
                                } else {
                                    mClassifyData.setPageNum(mClassifyData.getPageNum() + 1);
                                    List<BookShelf> bookShelfList = mClassifyData.getNovel_list();
                                    bookShelfList.addAll(classifyData.getNovel_list());
                                    mClassifyData.setNovel_list(bookShelfList);
                                    mClassifyDatas.put(classifyData.getCategory_id(), mClassifyData);
                                }
                            } else {
                                rightDataAdapter.setNotDataFooter();
                            }
                        }

                        //如果是当前页
                        if (classifyData.getCategory_id() == mMenus.get(knowClassifyPosition).getCategory_id()) {
                            book_sum.setText(String.format("共计%s本", classifyData.getNovel_count()));

                            ClassifyData knowClassifyData = mClassifyDatas.get(classifyData.getCategory_id());
                            List<BaseDataBean> mData;
                            if (knowClassifyData == null) {
                                mData = new ArrayList<>();
                            } else {
                                mData = new ArrayList<>(knowClassifyData.getNovel_list());
                            }
                            if (mData.size() < 1) {
                                no_book_show.setVisibility(View.VISIBLE);
                                book_sum.setText("");
                            } else {
                                no_book_show.setVisibility(View.GONE);
                            }
                            rightDataAdapter.setmDataList(mData);
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        ClassifyData mClassifyData = mClassifyDatas.get(mMenus.get(knowClassifyPosition).getCategory_id());
                        if (mClassifyData != null && mClassifyData.getNovel_list().size() > 0) {
                            no_book_show.setVisibility(View.GONE);
                        } else {
                            no_book_show.setVisibility(View.VISIBLE);
                            book_sum.setText("");
                        }
                        return null;
                    }
                }).execute();
    }
}
