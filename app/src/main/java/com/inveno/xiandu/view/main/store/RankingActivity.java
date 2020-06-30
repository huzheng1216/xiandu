package com.inveno.xiandu.view.main.store;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.datareport.manager.ReportManager;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.RankingData;
import com.inveno.xiandu.bean.book.RankingMenu;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.adapter.RankingLeftMenuAdapter;
import com.inveno.xiandu.view.adapter.RightDataAdapter;
import com.inveno.xiandu.view.search.SearchActivityMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.RANKING_LIST;

/**
 * @author yongji.wang
 * @date 2020/6/12 16:43
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_RANKING)
public class RankingActivity extends TitleBarBaseActivity {

    private TextView ranking_man_bt;
    private TextView ranking_woman_bt;
    private List<RankingMenu> mMenus = new ArrayList<>();
    private List<BaseDataBean> rankingBooks = new ArrayList<>();
    private RightDataAdapter rightDataAdapter;
    private RankingLeftMenuAdapter leftMenuAdapter;
    private int knowRankingPosition = 0;
    private int channel_id = 1;
    private boolean isBookEnd = false;
    private HashMap<String, List<RankingData>> mRankingDatas = new HashMap<>();
    private RecyclerView ranking_data_recycle;
    /**
     * 这个页面只有一个广告
     */
    private AdModel adModel;

    @Override
    public String getCenterText() {
        return "排行榜";
    }


    @Override
    public int layoutID() {
        return R.layout.activity_ranking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.ranking_bg, true);
        isBookEnd = getIntent().getBooleanExtra("isEndRanking", false);
    }

    @Override
    protected void initView() {
        super.initView();

        ranking_man_bt = findViewById(R.id.ranking_man_bt);
        ranking_woman_bt = findViewById(R.id.ranking_woman_bt);

        ranking_data_recycle = findViewById(R.id.ranking_data_recycle);
        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        ranking_data_recycle.setLayoutManager(dataLayoutManager);
        rightDataAdapter = new RightDataAdapter(this, this, rankingBooks);
        ranking_data_recycle.setAdapter(rightDataAdapter);
        rightDataAdapter.setOnitemClickListener(new RightDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseDataBean baseDataBean) {
                //根据id请求书籍内容后跳转
                if (baseDataBean instanceof RankingData) {
                    APIContext.getBookCityAPi().getBook(((RankingData) baseDataBean).getContent_id())
                            .onSuccess(new Function1<BookShelf, Unit>() {
                                @Override
                                public Unit invoke(BookShelf bookShelf) {
                                    Toaster.showToastShort(RankingActivity.this, "正在获取书籍，请稍等...");
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
                                    Toaster.showToastCenter(RankingActivity.this, "获取书籍失败：" + integer);
                                    return null;
                                }
                            }).execute();
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

        RecyclerView ranking_menu_recycle = findViewById(R.id.ranking_menu_recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ranking_menu_recycle.setLayoutManager(linearLayoutManager);
        leftMenuAdapter = new RankingLeftMenuAdapter(this, mMenus);
        ranking_menu_recycle.setAdapter(leftMenuAdapter);
        leftMenuAdapter.setOnitemClickListener(new RankingLeftMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (knowRankingPosition != position) {
                    knowRankingPosition = position;
                    choiseRanking();
                }
            }
        });
        getRankingMenu();
        loadAd();
        report();
    }

    @Override
    protected View getRightBtn() {
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        ClickUtil.bindSingleClick(imageView, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, SearchActivityMain.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(RankingActivity.this, imageView, "photo").toBundle();
                startActivity(intent, bundle);
            }
        });
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toaster.showToast(RankingActivity.this, "搜索");
//            }
//        });
        return imageView;
    }

    /**
     * 获取排行菜单
     */
    private void getRankingMenu() {
        APIContext.getBookCityAPi().getRankingMenu()
                .onSuccess(new Function1<List<RankingMenu>, Unit>() {
                    @Override
                    public Unit invoke(List<RankingMenu> rankingMenus) {
                        mMenus = rankingMenus;
                        leftMenuAdapter.setMenusData(mMenus);
                        knowRankingPosition = 0;
                        if (isBookEnd) {
                            for (int i = 0; i < mMenus.size(); i++) {
                                RankingMenu menu = mMenus.get(i);
                                if (menu.getRanking_name().equals("完结榜")) {
                                    knowRankingPosition = i;
                                    leftMenuAdapter.setSelectMenu(knowRankingPosition);
                                    break;
                                }
                            }
                        }
                        getRankingData();
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
     * 获取排行数据
     */
    private void getRankingData() {
        APIContext.getBookCityAPi().getRankingData(mMenus.get(knowRankingPosition).getRanking_id(), channel_id)
                .onSuccess(new Function1<List<RankingData>, Unit>() {
                    @Override
                    public Unit invoke(List<RankingData> rankingData) {
                        // TODO: 2020/6/17 这里先缓存记录分类数据，后续更改使用数据库存储
                        String dataKey = String.format("%s-%s", mMenus.get(knowRankingPosition).getRanking_id(), channel_id);
                        mRankingDatas.put(dataKey, rankingData);
                        List<BaseDataBean> mData = new ArrayList<>(rankingData);
                        addAd(mData);
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

    public void click_man(View view) {
        ranking_man_bt.setBackground(getResources().getDrawable(R.drawable.blue_round_bg_15));
        ranking_woman_bt.setBackground(getResources().getDrawable(R.drawable.gray_round_bg_15));
        ranking_man_bt.setTextColor(getResources().getColor(R.color.white));
        ranking_woman_bt.setTextColor(getResources().getColor(R.color.gray_6));

        if (channel_id != 1) {
            channel_id = 1;
            choiseRanking();
        }
    }

    public void click_woman(View view) {
        ranking_man_bt.setBackground(getResources().getDrawable(R.drawable.gray_round_bg_15));
        ranking_woman_bt.setBackground(getResources().getDrawable(R.drawable.blue_round_bg_15));
        ranking_man_bt.setTextColor(getResources().getColor(R.color.gray_6));
        ranking_woman_bt.setTextColor(getResources().getColor(R.color.white));

        if (channel_id != 2) {
            channel_id = 2;
            choiseRanking();
        }
    }

    public void choiseRanking() {
        rankingBooks.clear();
        rightDataAdapter.setmDataList(rankingBooks);
        rightDataAdapter.addAd(adModel);//先展示广告
        String dataKey = String.format("%s-%s", mMenus.get(knowRankingPosition).getRanking_id(), channel_id);
        List<RankingData> rankingDatas = mRankingDatas.get(dataKey);
        if (rankingDatas != null) {
            ArrayList<BaseDataBean> mData = new ArrayList<>(rankingDatas);
            addAd(mData);
            rightDataAdapter.setmDataList(mData);
        } else {
            getRankingData();
        }
    }


    /**
     * 加载广告
     */
    private void loadAd(){
        InvenoAdServiceHolder.getService().requestInfoAd(RANKING_LIST, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModel = new AdModel(wrapper);
            rightDataAdapter.addAd(adModel);
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }

    private void addAd(List<BaseDataBean> mData){
        if (adModel!=null) {
            int index = adModel.getWrapper().getIndex();
            if (mData.size() >= index){
                mData.add(index,adModel);
            }
        }
    }

    private void report(){
        ReportManager.INSTANCE.reportPageImp(5,"",this, ServiceContext.userService().getUserPid());
    }

    private void clickReport(long contentId) {
        ReportManager.INSTANCE.reportBookClick(6, "", "", 10,
                0, contentId, RankingActivity.this, ServiceContext.userService().getUserPid());
    }

    private void impReport(int first, int last) {
        List<BaseDataBean> mBookselfs = new ArrayList<>(rightDataAdapter.getmDataList());
        int size = mBookselfs.size();
        int newLast = last;
        Log.i("ReportManager", "size:" + size + " first:" + first + "  last:" + last + " newLast:"+newLast);
        //TODO
        if (size>0 && first<=0 && newLast<=0){
            first = 0;
            newLast = Math.min(7,size-1);
        }
        Log.i("ReportManager", "2    size:" + size + " first:" + first + "  newLast:" + newLast);
        if (first >= 0 && newLast >= 0 && size > newLast) {
            for (int i = first; i <= newLast; i++) {

                BaseDataBean baseDataBean = mBookselfs.get(i);
                if (baseDataBean instanceof RankingData) {
                    RankingData rankingData = (RankingData) baseDataBean;
                    Log.i("ReportManager", "name:" + rankingData.getBook_name());
                    long contentId = rankingData.getContent_id();
                    ReportManager.INSTANCE.reportBookImp(6, "", "", 10,
                            0, contentId, RankingActivity.this, ServiceContext.userService().getUserPid());
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
