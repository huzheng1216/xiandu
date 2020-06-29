package com.inveno.xiandu.view.read;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.appbar.AppBarLayout;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.SystemBarUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.ad.ADViewHolderFactory;
import com.inveno.xiandu.view.ad.holder.NormalAdViewHolder;
import com.inveno.xiandu.view.read.page.PageLoader;
import com.inveno.xiandu.view.read.page.PageView;
import com.inveno.xiandu.view.read.setting.BrightnessUtils;
import com.inveno.xiandu.view.read.setting.ReadSettingManager;
import com.inveno.xiandu.view.read.setting.ScreenUtils;
import com.inveno.xiandu.view.read.setting.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.VISIBLE;
import static com.inveno.android.ad.config.AdViewType.AD_READER_BETWEEN_TYPE;
import static com.inveno.android.ad.config.AdViewType.AD_READER_BOTTOM_TYPE;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BETWEEN;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BOTTOM;

/**
 * Created by zheng.hu on
 */
@Route(path = ARouterPath.ACTIVITY_AD_BETWEENT)
public class ADBetweenActivity extends BaseActivity {

    @BindView(R.id.layout_ad)
    FrameLayout adLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_between);
        ButterKnife.bind(this);
        
        initData();
    }

    private void initData() {


    }
}
