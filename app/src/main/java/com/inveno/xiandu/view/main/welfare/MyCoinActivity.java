package com.inveno.xiandu.view.main.welfare;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.applocation.MainApplication;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.coin.CoinDetail;
import com.inveno.xiandu.bean.coin.CoinDetailData;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.adapter.CoinDetailAdapter;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/11 16:16
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_MY_COIN)
public class MyCoinActivity extends BaseActivity {

    private TextView coin_balance;
    private TextView coin_today;
    private TextView coin_sum_get;
    private TextView coin_exchange_rate;
    private TextView no_coin_detail;
    private CoinDetailAdapter coinDetailAdapter;
    private TextView coin_detail_txt;
    private ArrayList<BaseDataBean> coinDetailDatas = new ArrayList<>();

    private int pageKnow = 0;
    private UserCoin mUserCoin;
    private String mUserCoinStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin);
        setStatusBar(R.color.my_coin_top_color, true);

        mUserCoinStr = getIntent().getStringExtra("mUserCoin");
        mUserCoin = GsonUtil.gsonToObject(mUserCoinStr, UserCoin.class);
        initView();
    }

    private void initView() {
        ImageView title_bar_back = findViewById(R.id.title_bar_back);
        title_bar_back.setColorFilter(Color.WHITE);
        coin_balance = findViewById(R.id.coin_balance);
        coin_today = findViewById(R.id.coin_today);
        coin_sum_get = findViewById(R.id.coin_sum_get);
        coin_exchange_rate = findViewById(R.id.coin_exchange_rate);
        no_coin_detail = findViewById(R.id.no_coin_detail);

        coin_detail_txt = findViewById(R.id.coin_detail_txt);
//        coin_detail_txt.setTypeface(MainApplication.getInstance().getSanhansTypeface());
//        TextPaint tp = coin_detail_txt.getPaint();
//        tp.setFakeBoldText(true);

        RecyclerView coin_detail_recycleview = findViewById(R.id.coin_detail_recycleview);
        coinDetailAdapter = new CoinDetailAdapter(this, this, coinDetailDatas);
        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        coin_detail_recycleview.setLayoutManager(dataLayoutManager);
        coin_detail_recycleview.setAdapter(coinDetailAdapter);

        //上拉加载
        coin_detail_recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    getCoinDetail();
                }
            }
        });
        if (mUserCoin != null) {
            coin_balance.setText(String.valueOf(mUserCoin.getBalance()));
            coin_today.setText(String.valueOf(mUserCoin.getCurrent_coin()));
            coin_sum_get.setText(String.valueOf(mUserCoin.getTotal_coin()));
            coin_exchange_rate.setText(mUserCoin.getExchage_rate());
        } else {
            if (ServiceContext.userService().getUserInfo() != null && ServiceContext.userService().getUserInfo().getPid() > 0) {
                getCoin();
            }
        }

        if (ServiceContext.userService().getUserInfo() != null && ServiceContext.userService().getUserInfo().getPid() > 0) {
            getCoinDetail();
        }
    }

    /**
     * 获取金币和明细
     */
    public void getCoin() {
        APIContext.coinApi().queryCoin()
                .onSuccess(new Function1<UserCoinOut, Unit>() {
                    @Override
                    public Unit invoke(UserCoinOut userCoinOut) {
                        UserCoin userCoin = userCoinOut.getCoin();
                        coin_balance.setText(String.valueOf(userCoin.getBalance()));
                        coin_today.setText(String.valueOf(userCoin.getCurrent_coin()));
                        coin_sum_get.setText(String.valueOf(userCoin.getTotal_coin()));
                        coin_exchange_rate.setText(userCoin.getExchage_rate());
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

    public void getCoinDetail() {
        APIContext.coinApi().getCoinDetail(pageKnow)
                .onSuccess(new Function1<CoinDetail, Unit>() {
                    @Override
                    public Unit invoke(CoinDetail coinDetail) {
                        //索引从0开始计算，不需要+1
                        pageKnow = coinDetail.getPage() + coinDetail.getPageSize();

                        if (coinDetail.getCoin_detail().size() < coinDetail.getPageSize()) {
                            coinDetailAdapter.setFooterText("没有更多数据");
                        } else {
                            coinDetailAdapter.setFooterText("正在努力加载...");
                        }
                        coinDetailDatas.addAll(coinDetail.getCoin_detail());
                        coinDetailAdapter.setData(coinDetailDatas);
                        if (coinDetailDatas.size() > 0) {
                            no_coin_detail.setVisibility(View.GONE);
                        } else {
                            no_coin_detail.setVisibility(View.VISIBLE);
                        }
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        no_coin_detail.setVisibility(View.VISIBLE);
                        return null;
                    }
                }).execute();
    }

    public void back(View view) {
        finish();
    }

    public void coin_top_up(View view) {
        ARouter.getInstance().build(ARouterPath.ACTIVITY_COIN_TOP_UP).withString("mUserCoin", mUserCoinStr).navigation();
    }
}
