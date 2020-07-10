package com.inveno.xiandu.view.main.store;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
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
import com.inveno.xiandu.view.detail.BookDetailActivity;

import java.time.LocalDate;
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
public class ClassifyItemFragment extends BaseFragment implements View.OnClickListener {
    private int channel;

    private RelativeLayout classify_screen;
    private LinearLayout classify_screen_top;
    private TextView book_sum;
    private TextView book_screen;
    private TextView screen_no;
    private TextView screen_load;
    private TextView screen_over;
    private TextView screen_cancel;
    private RecyclerView ranking_data_recycle;

    //    private TextView no_book_show;
    private RightDataAdapter rightDataAdapter;
    private LeftMenuAdapter leftMenuAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<ClassifyMenu> mMenus = new ArrayList<>();
    private List<BaseDataBean> mBookselfs = new ArrayList<>();

    //选择的分类
    private int selectPosition = 0;
    //分类id
    private int selectCategoryId = 0;
    //    -1表示全部，0:连载 ,1:完本
    private int book_status = -1;
    private boolean isOpenScreen = false;
    //菜单是否加载成功
    private boolean isMenuLoad = false;

    private HashMap<String, ClassifyData> mClassifyDatas = new HashMap<>();

    private PopupWindow popupWindow;
    private View contentView;

    private AdModel adModel;

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
        classify_screen = view.findViewById(R.id.classify_screen);
        classify_screen.setOnClickListener(this);
        classify_screen_top = view.findViewById(R.id.classify_screen_top);
        book_sum = view.findViewById(R.id.book_sum);

        screen_no = view.findViewById(R.id.screen_no);
        screen_no.setOnClickListener(this);
        screen_load = view.findViewById(R.id.screen_load);
        screen_load.setOnClickListener(this);
        screen_over = view.findViewById(R.id.screen_over);
        screen_over.setOnClickListener(this);
        screen_cancel = view.findViewById(R.id.screen_cancel);
        screen_cancel.setOnClickListener(this);

        book_screen = view.findViewById(R.id.book_screen);
        book_screen.setOnClickListener(this);
//        no_book_show = view.findViewById(R.id.no_book_show);
        book_sum.setText("共计0本");
        ranking_data_recycle = view.findViewById(R.id.ranking_data_recycle);
        RecyclerView ranking_menu_recycle = view.findViewById(R.id.ranking_menu_recycle);

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(getActivity());
        ranking_data_recycle.setLayoutManager(dataLayoutManager);
        rightDataAdapter = new RightDataAdapter(getActivity(), getActivity(), mBookselfs);
        rightDataAdapter.setFooterView(getViewHolderView(getContext(), R.layout.item_right_load_more));
        ranking_data_recycle.setAdapter(rightDataAdapter);
        ranking_data_recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    impReport();
                }
            }
        });
        rightDataAdapter.setOnitemClickListener(new RightDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean) {
                if (baseDataBean instanceof BookShelf) {
                    Toaster.showToastShort(getContext(), "正在获取书籍，请稍等...");
                    BookShelf bookShelf = (BookShelf) baseDataBean;
                    APIContext.getBookCityAPi().getBook(bookShelf.getContent_id())
                            .onSuccess(new Function1<BookShelf, Unit>() {
                                @Override
                                public Unit invoke(BookShelf bookShelf) {
                                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                                            .withString("json", GsonUtil.objectToJson(bookShelf))
                                            .navigation();
                                    clickReport(bookShelf.getContent_id());
                                    return null;
                                }
                            })
                            .onFail(new Function2<Integer, String, Unit>() {
                                @Override
                                public Unit invoke(Integer integer, String s) {
                                    Toaster.showToastCenter(getContext(), "获取书籍失败");
                                    return null;
                                }
                            }).execute();
//                    ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
//                            .withString("json", GsonUtil.objectToJson(bookShelf))
//                            .navigation();
//                    clickReport(bookShelf.getContent_id());
                }
            }
        });
        //TODO 这里可能内存泄漏
        ranking_data_recycle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                impReport();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        ranking_menu_recycle.setLayoutManager(linearLayoutManager);
        leftMenuAdapter = new LeftMenuAdapter(getActivity(), mMenus);
        ranking_menu_recycle.setAdapter(leftMenuAdapter);
        leftMenuAdapter.setOnitemClickListener(new LeftMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (selectPosition != position) {
                    rightDataAdapter.reLoad();
                    //点击后列表初始化，准备填充新数据
                    selectPosition = position;
                    selectCategoryId = mMenus.get(selectPosition).getCategory_id();
                    getBookData();
                }
            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isMenuLoad) {
                    //菜单未加载，则需要加载
                    loadMenuData();
                } else {
                    //刷新要清掉旧数据
                    //点击后列表初始化，准备填充新数据
                    mBookselfs.clear();
                    rightDataAdapter.setmDataList(mBookselfs);
                    initAd();//先展示广告
                    book_sum.setText("共计0本");
                    //本地缓存也要清除
                    mClassifyDatas.remove(selectCategoryId + "-" + book_status);
                    //刷新请求新数据
                    getClassifyData(1);
                }
            }
        });
        //上拉加载
        ranking_data_recycle.addOnScrollListener(new MRecycleScrollListener() {
            @Override
            public void onLoadMore() {
//                Toaster.showToastShort(SerchResultActivity.this, "上拉加载");
                if (!rightDataAdapter.isNotMore()) {
                    //拿到缓存数据，确定要加载的页码
                    ClassifyData mClassifyData = mClassifyDatas.get(getDataKey(selectCategoryId));
                    if (mClassifyData != null) {
                        //本地有缓存，从缓存的页码开始加载
                        getClassifyData(mClassifyData.getPageNum());
//                        mBookselfs = new ArrayList<>(mClassifyData.getNovel_list());
//                        if (adModel != null && mBookselfs.size() >= adModel.getWrapper().getIndex()) {
//                            mBookselfs.add(adModel.getWrapper().getIndex(), adModel);
//                        }
//                        rightDataAdapter.setmDataList(mBookselfs);
                    } else {
                        //本地无缓存，从第一页开始加载
                        getClassifyData(1);
                    }
                }
            }

            @Override
            public void onVisibleItem(int first, int last) {

            }
        });
        initPopwindow();

        return view;
    }

    /**
     * 获取缓存数据的key
     *
     * @param categoryId 分类id
     * @return
     */
    private String getDataKey(int categoryId) {
        return categoryId + "-" + book_status;
    }

    private void getBookData() {
        mBookselfs.clear();
        rightDataAdapter.setmDataList(mBookselfs);
        book_sum.setText("共计0本");
        initAd();//先展示广告
        //先从缓存里面拿
        ClassifyData classifyData = mClassifyDatas.get(selectCategoryId + "-" + book_status);

        if (classifyData != null) {
            //缓存有数据，直接填充
            mBookselfs = new ArrayList<>(classifyData.getNovel_list());
            rightDataAdapter.setmDataList(mBookselfs);
            book_sum.setText(String.format("共计%s本", classifyData.getNovel_count()));
            if (mBookselfs.size() < 1) {
//                            no_book_show.setVisibility(View.VISIBLE);
//                            book_sum.setText("");
            } else {
//                            no_book_show.setVisibility(View.GONE);
            }
            addAd();//先展示广告
        } else {
            //缓存没数据，需要去请求
            getClassifyData(1);
        }

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
        if (getActivity() instanceof ClassifyActivity) {
            ((ClassifyActivity) getActivity()).setBackPressedClickListener(new ClassifyActivity.OnBackPressedClickListener() {
                @Override
                public void onBackPressed() {
                    onFragmentBackPressed();
                }
            });
        }
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        //第一次加载数据
        Log.i("ClassifyItemFragment", "页面: " + channel);
        Log.i("ClassifyItemFragment", "是否第一次: " + firstVisble);
        if (firstVisble && !isMenuLoad) {
            //先加载分类菜单
            loadMenuData();
        }
    }

    private void loadMenuData() {
        APIContext.getBookCityAPi().getClassifyMenu(channel)
                .onSuccess(new Function1<List<ClassifyMenu>, Unit>() {
                    @Override
                    public Unit invoke(List<ClassifyMenu> classifyMenus) {
                        isMenuLoad = true;
                        mMenus = classifyMenus;
                        leftMenuAdapter.setMenusData(mMenus);
                        selectPosition = 0;
                        selectCategoryId = mMenus.get(0).getCategory_id();
                        initAd();//先展示广告
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

    /**
     * 获取分类数据
     */
    private void getClassifyData(int page_num) {
        APIContext.getBookCityAPi().getClassifyData(selectCategoryId, book_status, page_num)
                .onSuccess(new Function1<ClassifyData, Unit>() {
                    @Override
                    public Unit invoke(ClassifyData classifyData) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        // TODO: 2020/6/17 这里先缓存记录分类数据，后续更改使用数据库存储
//                        ClassifyData mClassifyData = mClassifyDatas.get(getDataKey());
//                        //是否又记录数据
//                        if (mClassifyData == null) {
//                            //本地无数据，直接添加缓存
//                            classifyData.setPageNum(2);
//                            mClassifyDatas.put(classifyData.getCategory_id() + "-" + book_status, classifyData);
//                        } else {
//                            //本地有数据
//                            //请求有内容，往缓存数据里面去增加内容
//                            if (classifyData.getNovel_list().size() > 0) {
//                                //本地数据是否够一页显示
//                                if (mClassifyData.getNovel_list().size() < 1) {
//                                    classifyData.setPageNum(2);
//                                    mClassifyData.getNovel_list().addAll(classifyData.getNovel_list());
////                                    mClassifyDatas.put(classifyData.getCategory_id() + "-" + book_status, classifyData);
//                                } else {
//                                    mClassifyData.setPageNum(mClassifyData.getPageNum() + 1);
//
//                                    //分页加载，所以存储数据需要累加
//                                    if (mClassifyData.getNovel_list() != null) {
//                                        mClassifyData.getNovel_list().addAll(classifyData.getNovel_list());
//                                    }
////                                    List<BookShelf> bookShelfList = mClassifyData.getNovel_list();
////                                    bookShelfList.addAll(classifyData.getNovel_list());
////                                    mClassifyData.setNovel_list(bookShelfList);
////                                    mClassifyDatas.put(classifyData.getCategory_id() + "-" + book_status, mClassifyData);
//                                }
//                            } else {
//                                rightDataAdapter.setNotDataFooter();
//                            }
//                        }

                        //1.从本地拿数据
                        ClassifyData localData = mClassifyDatas.get(getDataKey(classifyData.getCategory_id()));
                        if (localData != null && localData.getNovel_list().size() > 0) {
                            //本地有数据
                            if (classifyData.getNovel_list()!=null && classifyData.getNovel_list().size()>0){
                                //请求有数据
                                //设置加载的页码
                                localData.setPageNum(localData.getPageNum() + 1);
                                localData.getNovel_list().addAll(classifyData.getNovel_list());
                            }else{
                                //请求无数据
                                rightDataAdapter.setNotDataFooter();
                            }
                        } else {
                            //本地没有数据
                            //本地有数据
                            if (classifyData.getNovel_list()!=null && classifyData.getNovel_list().size()>0) {
                                //请求有数据
                                //设置加载的页码
                                classifyData.setPageNum(2);
                                mClassifyDatas.put(getDataKey(classifyData.getCategory_id()), classifyData);
                            }else{
                                //请求无数据
                                rightDataAdapter.setNotDataFooter();
                            }
                        }
                        //如果是当前页
                        if (classifyData.getCategory_id() == selectCategoryId) {
                            book_sum.setText(String.format("共计%s本", classifyData.getNovel_count()));

                            ClassifyData knowClassifyData = mClassifyDatas.get(classifyData.getCategory_id() + "-" + book_status);
                            List<BaseDataBean> mData;
                            if (knowClassifyData == null) {
                                mData = new ArrayList<>();
                            } else {
                                mData = new ArrayList<>(knowClassifyData.getNovel_list());
                            }
                            if (mData.size() < 1) {
//                                no_book_show.setVisibility(View.VISIBLE);
//                                book_sum.setText("");
                            } else {
//                                no_book_show.setVisibility(View.GONE);
                            }
                            rightDataAdapter.setmDataList(mData);
                        }
//                        mClassifyDatas.put(classifyData.getCategory_id() + "-" + book_status, classifyData);
                        ClassifyData showData = mClassifyDatas.get(getDataKey(selectCategoryId));
                        if (showData != null) {
                            List<BaseDataBean> mData = new ArrayList<>(showData.getNovel_list());
                            //加广告
                            if (adModel != null && mData.size() >= adModel.getWrapper().getIndex()) {
                                mData.add(adModel.getWrapper().getIndex(), adModel);
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
                        ClassifyData mClassifyData = mClassifyDatas.get(getDataKey(selectCategoryId));
                        rightDataAdapter.setNotDataFooter();
                        if (mClassifyData != null && mClassifyData.getNovel_list().size() > 0) {
//                            no_book_show.setVisibility(View.GONE);
                        } else {
//                            no_book_show.setVisibility(View.VISIBLE);
//                            book_sum.setText("");
                        }
                        return null;
                    }
                }).execute();
    }

    public void notifyAdSetChanged(AdModel adModel) {
        this.adModel = adModel;
        int index = adModel.getWrapper().getIndex();
        if (mBookselfs != null && mBookselfs.size() >= index) {
            mBookselfs.add(index, adModel);
            rightDataAdapter.setmDataList(mBookselfs);
        }
    }

    private void addAd() {
        if (adModel != null && adModel.getWrapper() != null) {
            int index = adModel.getWrapper().getIndex();
            if (mBookselfs != null && mBookselfs.size() >= index) {
                mBookselfs.add(index, adModel);
                rightDataAdapter.setmDataList(mBookselfs);
            }
        }
    }

    public void openPopWindow() {
        //从底部显示
        popupWindow.showAtLocation(contentView, Gravity.TOP, 0, 0);
        //添加按键事件监听
//        setButtonListeners();
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f, getContext());
            }
        });
        setBackgroundAlpha(0.5f, getContext());
    }

    /**
     * 显示popupWindow
     */
    private void initPopwindow() {
        //加载弹出框的布局
        contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.pop_classfiy_screen, null);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_classify_style);
        // 按下android回退物理键 PopipWindow消失解决

    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    private void setButtonListeners() {
        TextView man = contentView.findViewById(R.id.man);
        TextView women = contentView.findViewById(R.id.women);
        TextView cancel = contentView.findViewById(R.id.cancel);
    }


    /**
     * 固定广告位置
     */
    private void initAd() {
        if (adModel != null) {
            int index = adModel.getWrapper().getIndex();
            if (mBookselfs != null && mBookselfs.size() == 0 && index == 0) {
                mBookselfs.add(adModel);
                rightDataAdapter.setmDataList(mBookselfs);
            }
        }
    }

    private void setScreenBg(TextView textView) {
        screen_no.setTextColor(getResources().getColor(R.color.gray_6));
        screen_load.setTextColor(getResources().getColor(R.color.gray_6));
        screen_over.setTextColor(getResources().getColor(R.color.gray_6));

        screen_no.setBackground(null);
        screen_load.setBackground(null);
        screen_over.setBackground(null);

        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackground(getResources().getDrawable(R.drawable.blue_round_bg_25));

        classify_screen_top.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pophidden_left_to_right_anim));
        classify_screen.setVisibility(View.GONE);
        isOpenScreen = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.book_screen) {
            classify_screen_top.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popshow_right_to_left_anim));
            classify_screen.setVisibility(View.VISIBLE);
            isOpenScreen = true;
        } else if (id == R.id.screen_no) {
            setScreenBg(screen_no);
            book_status = -1;
            getBookData();
        } else if (id == R.id.screen_load) {
            setScreenBg(screen_load);
            book_status = 0;
            getBookData();
        } else if (id == R.id.screen_over) {
            setScreenBg(screen_over);
            book_status = 1;
            getBookData();
        } else if (id == R.id.screen_cancel) {
            classify_screen_top.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pophidden_left_to_right_anim));
            classify_screen.setVisibility(View.GONE);
            isOpenScreen = false;
        } else if (id == R.id.classify_screen) {
            classify_screen_top.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pophidden_left_to_right_anim));
            classify_screen.setVisibility(View.GONE);
            isOpenScreen = false;
        }
    }

    private void onFragmentBackPressed() {
        if (isOpenScreen) {
            classify_screen_top.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pophidden_left_to_right_anim));
            classify_screen.setVisibility(View.GONE);
            isOpenScreen = false;
        } else {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private void clickReport(long contentId) {
        ReportManager.INSTANCE.reportBookClick(6, "", "", 10,
                0, contentId, getContext(), ServiceContext.userService().getUserPid());
    }

    private void impReport(int first, int last) {
        List<BaseDataBean> mBookselfs = new ArrayList<>(rightDataAdapter.getmDataList());
        int size = mBookselfs.size();
        int newLast = last - 1;
//        Log.i("ReportManager", "size:" + size + " first:" + first + "  last:" + last + " newLast:" + newLast);
        if (first >= 0 && newLast >= 0 && size > newLast) {
            for (int i = first; i <= newLast; i++) {
                BaseDataBean baseDataBean = mBookselfs.get(i);
                if (baseDataBean instanceof BookShelf) {
                    BookShelf bookShelf = (BookShelf) baseDataBean;
//                    Log.i("ReportManager", "name:" + bookShelf.getBook_name());
                    long contentId = bookShelf.getContent_id();
                    ReportManager.INSTANCE.reportBookImp(6, "", "", 10,
                            0, contentId, getContext(), ServiceContext.userService().getUserPid());
                }

            }
        }
    }

    private void impReport() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) ranking_data_recycle.getLayoutManager();
        if (layoutManager != null) {
            impReport(layoutManager.findFirstCompletelyVisibleItemPosition(), layoutManager.findLastVisibleItemPosition());
        }
    }
}
