package com.inveno.xiandu.view.search;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.StringTools;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.components.DelayerEditText;
import com.inveno.xiandu.view.components.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 搜索
 */
@Route(path = ARouterPath.ACTIVITY_SEARCH)
public class SerchActivityMain extends BaseActivity {

    private HistoryAdapter historyAdapter;
    private ArrayList<String> history;

    @BindView(R.id.edit_search_main)
    DelayerEditText editText;
    @BindView(R.id.recycleview_search_main_history)
    RecyclerView historyRecyclerView;
    @BindView(R.id.bt_search_main_history_del)
    View del;
    @BindView(R.id.bt_search_main_back)
    View back;
    @BindView(R.id.bt_search_main_cancel)
    View bt_search_main_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_main);
        ButterKnife.bind(this);
        setStatusBar(R.color.white, true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);  // 垂直排列
        historyRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, getResources().getDimensionPixelSize(R.dimen.adapter_search_history), true));
        historyRecyclerView.setLayoutManager(layoutManager);
        editText.setOnDelayerTextChange(new DelayerEditText.OnDelayerTextChange() {
            @Override
            public void textChange(String title) {
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(editText.getText().toString());
                }
                return false;
            }
        });
        ClickUtil.bindSingleClick(del, 300, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history.clear();
                historyAdapter.notifyDataSetChanged();
                SPUtils.setInformain(Keys.SEARCH_HISTORY, "");
            }
        });
        ClickUtil.bindSingleClick(back, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SerchActivityMain.this);
            }
        });
        ClickUtil.bindSingleClick(bt_search_main_cancel, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SerchActivityMain.this);
            }
        });
        //本地历史
        String localHistory = SPUtils.getInformain(Keys.SEARCH_HISTORY, "");
        history = new ArrayList<>();
        List<String> strings = GsonUtil.gsonToList(localHistory, String.class);
        if (strings != null) {
            history.addAll(strings);
        }
        historyAdapter = new HistoryAdapter(history);
        historyAdapter.setClickListener(new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onClick(String title) {
                search(title);
            }

            @Override
            public void onLongClick(String title) {
                editText.setText(title);
                //将光标移至文字末尾
                editText.setSelection(title.length());
            }
        });
        historyRecyclerView.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        editText.requestFocus();
    }

    private void search(String title) {
        //TODO 搜索
        ARouter.getInstance().build(ARouterPath.ACTIVITY_SEARCH_RESULT)
                .withString("name", title)
                .navigation();
        if (history.contains(title)) {
            history.remove(title);
        }
        history.add(0, title);
        historyAdapter.notifyDataSetChanged();
        SPUtils.setInformain(Keys.SEARCH_HISTORY, GsonUtil.objectToJson(history));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(SerchActivityMain.this);
    }
}
