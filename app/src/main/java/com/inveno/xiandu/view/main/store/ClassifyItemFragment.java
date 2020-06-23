package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ClassifyData;
import com.inveno.xiandu.bean.book.ClassifyMenu;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.adapter.LeftMenuAdapter;
import com.inveno.xiandu.view.adapter.RightDataAdapter;

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
    private RecyclerView ranking_data_recycle;
    private RecyclerView ranking_menu_recycle;
    private RightDataAdapter rightDataAdapter;
    private LeftMenuAdapter leftMenuAdapter;

    private List<ClassifyMenu> mMenus = new ArrayList<>();
    private List<BaseDataBean> mBookself = new ArrayList<>();

    private int knowClassifyPosition = 0;
    private int book_status = -1;
    private int page_num = 1;

    private HashMap<Integer, ClassifyData> mClassifyDatas = new HashMap<>();

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
        book_sum = view.findViewById(R.id.book_sum);
        book_sum.setText("共计0本");
        ranking_data_recycle = view.findViewById(R.id.ranking_data_recycle);
        ranking_menu_recycle = view.findViewById(R.id.ranking_menu_recycle);

        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(getActivity());
        ranking_data_recycle.setLayoutManager(dataLayoutManager);
        rightDataAdapter = new RightDataAdapter(getActivity(), getActivity(), mBookself);
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
                    knowClassifyPosition = position;
                    mBookself.clear();
                    rightDataAdapter.setmDataList(mBookself);
                    book_sum.setText("共计0本");
                    initAd();//先展示广告
                    ClassifyData classifyData = mClassifyDatas.get(mMenus.get(knowClassifyPosition).getCategory_id());
                    if (classifyData != null) {
                        mBookself = new ArrayList<>(classifyData.getNovel_list());
                        if (adModel != null && mBookself.size() >= adModel.getWrapper().getIndex()) {
                            mBookself.add(adModel.getWrapper().getIndex(), adModel);
                        }
                        rightDataAdapter.setmDataList(mBookself);
                    } else {
                        getClassifyData();
                    }
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
                            initAd();//先展示广告
                            getClassifyData();
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
    private void getClassifyData() {
        APIContext.getBookCityAPi().getClassifyData(mMenus.get(knowClassifyPosition).getCategory_id(), book_status, page_num)
                .onSuccess(new Function1<ClassifyData, Unit>() {
                    @Override
                    public Unit invoke(ClassifyData classifyData) {
                        page_num++;
                        book_sum.setText(String.format("共计%s本", classifyData.getNovel_count()));
                        // TODO: 2020/6/17 这里先缓存记录分类数据，后续更改使用数据库存储
                        mClassifyDatas.put(classifyData.getCategory_id(), classifyData);
                        List<BaseDataBean> mData = new ArrayList<>(classifyData.getNovel_list());
                        //加广告
                        if (adModel != null && mData.size() >= adModel.getWrapper().getIndex()) {
                            mData.add(adModel.getWrapper().getIndex(), adModel);
                        }
                        rightDataAdapter.setmDataList(mData);
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

    public void notifyAdSetChanged(AdModel adModel) {
        this.adModel = adModel;
        int index = adModel.getWrapper().getIndex();
        if (mBookself != null && mBookself.size() >= index) {
            mBookself.add(index, adModel);
            rightDataAdapter.setmDataList(mBookself);
        }
    }

    /**
     * 固定广告位置
     */
    private void initAd(){
        if (adModel!=null) {
            int index = adModel.getWrapper().getIndex();
            if (mBookself != null && mBookself.size() == 0 && index == 0) {
                mBookself.add(adModel);
                rightDataAdapter.setmDataList(mBookself);
            }
        }
    }
}
