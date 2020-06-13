package com.inveno.xiandu.view.main.store;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.store.RankingBean;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.adapter.LeftMenuAdapter;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;
import com.inveno.xiandu.view.adapter.RightDataAdapter;
import com.inveno.xiandu.view.search.SerchActivityMain;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/12 16:43
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_CLASSIFY)
public class ClassifyActivity extends BaseActivity {

    private TextView ranking_man_bt;
    private TextView ranking_woman_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.ranking_bg, true);
        initView();
    }

    ArrayList<String> mNames = new ArrayList<>();
    ArrayList<BaseDataBean> testBean = new ArrayList<>();

    protected void initView() {
        mNames.add("高分榜");
        mNames.add("完结榜");
        mNames.add("人气榜");
        mNames.add("热搜榜");
        mNames.add("新书榜");

        for (int i = 0; i < 20; i++) {
            RankingBean rankingBean = new RankingBean();
            rankingBean.setType(RecyclerBaseAdapter.CONTENT_ITEM_TYPE);
            rankingBean.setRankBookname("小说排名" + i);
            rankingBean.setRankBookType("小说分类"+ i);
            testBean.add(rankingBean);
        }

        ranking_man_bt = findViewById(R.id.ranking_man_bt);
        ranking_woman_bt = findViewById(R.id.ranking_woman_bt);

        RecyclerView ranking_data_recycle = findViewById(R.id.ranking_data_recycle);
        LinearLayoutManager dataLayoutManager = new LinearLayoutManager(this);
        ranking_data_recycle.setLayoutManager(dataLayoutManager);
        RightDataAdapter rightDataAdapter = new RightDataAdapter(this, this,testBean);
        ranking_data_recycle.setAdapter(rightDataAdapter);

        RecyclerView ranking_menu_recycle = findViewById(R.id.ranking_menu_recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ranking_menu_recycle.setLayoutManager(linearLayoutManager);
        LeftMenuAdapter leftMenuAdapter = new LeftMenuAdapter(this, mNames);
        ranking_menu_recycle.setAdapter(leftMenuAdapter);
        leftMenuAdapter.setOnitemClickListener(new LeftMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                Toaster.showToast(ClassifyActivity.this, "点击了：" + name);
            }
        });
    }

    protected View getRightBtn() {
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        ClickUtil.bindSingleClick(imageView, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this, SerchActivityMain.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(ClassifyActivity.this, imageView, "photo").toBundle();
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

    public void click_man(View view) {
        Toaster.showToast(this, "点击男频");
        ranking_man_bt.setBackground(getResources().getDrawable(R.drawable.blue_round_bg_15));
        ranking_woman_bt.setBackground(getResources().getDrawable(R.drawable.gray_round_bg_15));
        ranking_man_bt.setTextColor(getResources().getColor(R.color.white));
        ranking_woman_bt.setTextColor(getResources().getColor(R.color.gray_6));
    }

    public void click_woman(View view) {
        Toaster.showToast(this, "点击女频");
        ranking_man_bt.setBackground(getResources().getDrawable(R.drawable.gray_round_bg_15));
        ranking_woman_bt.setBackground(getResources().getDrawable(R.drawable.blue_round_bg_15));
        ranking_man_bt.setTextColor(getResources().getColor(R.color.gray_6));
        ranking_woman_bt.setTextColor(getResources().getColor(R.color.white));
    }
}
