package com.inveno.xiandu.view.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.config.Keys;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.StringTools;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.components.DelayerEditText;
import com.inveno.xiandu.view.components.GridSpacingItemDecoration;
import com.inveno.xiandu.view.dialog.IosTypeDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.inveno.android.ad.config.AdViewType.AD_SEARCH_TYPE;
import static com.inveno.android.ad.config.ScenarioManifest.RANKING_LIST;
import static com.inveno.android.ad.config.ScenarioManifest.SEARCH;

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
    @BindView(R.id.search_ad_ll)
    LinearLayout search_ad_ll;

    private AdModel adModel;

    private IosTypeDialog iosTypeDialog;

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
                //删除搜索历史二次确认弹窗
                IosTypeDialog.Builder builder = new IosTypeDialog.Builder(SerchActivityMain.this);
                builder.setContext("确定要删除搜索历史吗？");
                builder.setTitle("提示");
                builder.setLeftButton("确定", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog=null;
                        history.clear();
                        historyAdapter.notifyDataSetChanged();
                        del.setVisibility(View.GONE);
                        SPUtils.setInformain(Keys.SEARCH_HISTORY, "");
                    }
                });
                builder.setRightButton("取消", new IosTypeDialog.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosTypeDialog.dismiss();
                        iosTypeDialog=null;
                    }
                });

                iosTypeDialog = builder.create();

                iosTypeDialog.show();
                setDialogWindowAttr(iosTypeDialog);
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
        if (history.size()<1){
            del.setVisibility(View.GONE);
        }else{
            del.setVisibility(View.VISIBLE);
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
        loadAd();
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
        del.setVisibility(View.VISIBLE);
        SPUtils.setInformain(Keys.SEARCH_HISTORY, GsonUtil.objectToJson(history));
    }

    //在dialog.show()之后调用
    public void setDialogWindowAttr(Dialog dlg) {
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = (int)(windowManager.getDefaultDisplay().getWidth()* 0.8); //设置宽度
        dlg.getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(SerchActivityMain.this);
    }

    /**
     * 加载广告
     */
    private void loadAd(){
        InvenoAdServiceHolder.getService().requestInfoAd(SEARCH, this).onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModel = new AdModel(wrapper);
            NormalAdViewHolder holder = ((NormalAdViewHolder)ADViewHolderFactory.create(SerchActivityMain.this, AD_SEARCH_TYPE));
            holder.onBindViewHolder(SerchActivityMain.this,adModel.getWrapper().getAdValue(),0);
            ViewGroup view = holder.getViewGroup();
            search_ad_ll.addView(view);
            search_ad_ll.setVisibility(View.VISIBLE);
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        }).execute();
    }
}
