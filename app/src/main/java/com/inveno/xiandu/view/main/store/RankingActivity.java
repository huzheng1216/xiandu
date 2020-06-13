package com.inveno.xiandu.view.main.store;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.adapter.LeftMenuAdapter;
import com.inveno.xiandu.view.search.SerchActivityMain;

import java.util.ArrayList;

/**
 * @author yongji.wang
 * @date 2020/6/12 16:43
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_RANKING)
public class RankingActivity extends TitleBarBaseActivity {

    private RecyclerView ranking_menu_recycle;
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
    }

    ArrayList<String> mNames = new ArrayList<>();
    @Override
    protected void initView() {
        super.initView();
        mNames.add("高分榜");
        mNames.add("完结榜");
        mNames.add("人气榜");
        mNames.add("热搜榜");
        mNames.add("新书榜");
        ranking_menu_recycle = findViewById(R.id.ranking_menu_recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ranking_menu_recycle.setLayoutManager(linearLayoutManager);
        LeftMenuAdapter leftMenuAdapter = new LeftMenuAdapter(this, mNames);
        ranking_menu_recycle.setAdapter(leftMenuAdapter);
        leftMenuAdapter.setOnitemClickListener(new LeftMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                Toaster.showToast(RankingActivity.this,"点击了："+ name);
            }
        });
    }

    @Override
    protected View getRightBtn() {
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        ClickUtil.bindSingleClick(imageView, 200, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, SerchActivityMain.class);
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
}
