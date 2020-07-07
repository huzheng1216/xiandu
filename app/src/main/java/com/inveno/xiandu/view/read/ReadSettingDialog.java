package com.inveno.xiandu.view.read;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.view.read.adapter.PageStyleAdapter;
import com.inveno.xiandu.view.read.page.ComposeMode;
import com.inveno.xiandu.view.read.page.PageLoader;
import com.inveno.xiandu.view.read.page.PageMode;
import com.inveno.xiandu.view.read.page.PageStyle;
import com.inveno.xiandu.view.read.setting.BrightnessUtils;
import com.inveno.xiandu.view.read.setting.ReadSettingManager;
import com.inveno.xiandu.view.read.setting.ScreenUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zheng.hu on 17-5-18.
 */

public class ReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";
    private static final int DEFAULT_TEXT_SIZE = 21;

    @BindView(R.id.read_setting_iv_brightness_minus)
    ImageView mIvBrightnessMinus;
    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.read_setting_iv_brightness_plus)
    ImageView mIvBrightnessPlus;
    @BindView(R.id.read_setting_cb_brightness_auto)
    CheckBox mCbBrightnessAuto;
    @BindView(R.id.read_setting_tv_font_minus)
    TextView mTvFontMinus;
    @BindView(R.id.read_setting_tv_font)
    TextView mTvFont;
    @BindView(R.id.read_setting_tv_font_plus)
    TextView mTvFontPlus;
    @BindView(R.id.read_setting_cb_font_default)
    CheckBox mCbFontDefault;
    @BindView(R.id.read_setting_rg_page_mode)
    RadioGroup mRgPageMode;
    @BindView(R.id.read_setting_rg_page_compose)
    RadioGroup mRgPageCompose;

    @BindView(R.id.read_setting_rb_simulation)
    RadioButton mRbSimulation;
    @BindView(R.id.read_setting_rb_cover)
    RadioButton mRbCover;
    @BindView(R.id.read_setting_rb_slide)
    RadioButton mRbSlide;
    @BindView(R.id.read_setting_rb_scroll)
    RadioButton mRbScroll;
    @BindView(R.id.read_setting_rb_none)
    RadioButton mRbNone;
    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;

    //    @BindView(R.id.read_setting_rb_compose_4)
//    RadioButton mRbCompose4;
    @BindView(R.id.read_setting_rb_compose_3)
    RadioButton mRbCompose3;
    @BindView(R.id.read_setting_rb_compose_2)
    RadioButton mRbCompose2;
    @BindView(R.id.read_setting_rb_compose_0)
    RadioButton mRbCompose0;
//    @BindView(R.id.read_setting_tv_more)
//    TextView mTvMore;
    /************************************/
    private PageStyleAdapter mPageStyleAdapter;
    private ReadSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;
    private OnSettingListener onSettingListener;

    private PageMode mPageMode;
    private ComposeMode mComposeMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;

    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    public ReadSettingDialog(@NonNull Activity activity, PageLoader mPageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = mPageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mComposeMode = mSettingManager.getComposeMode();
        mPageStyle = mSettingManager.getPageStyle();
    }

    private void initWidget() {
        mSbBrightness.setProgress(mBrightness);
        mTvFont.setText(mTextSize + "");
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        mCbFontDefault.setChecked(isTextDefault);
        initPageMode();
        initPageCompose();
        //RecyclerView
        setUpAdapter();
    }

    private void setUpAdapter() {
        Drawable[] drawables = {
                getDrawable(R.color.nb_read_bg_1)
                , getDrawable(R.color.nb_read_bg_2)
                , getDrawable(R.color.nb_read_bg_3)
                , getDrawable(R.color.nb_read_bg_4)
                , getDrawable(R.color.nb_read_bg_5)};

        mPageStyleAdapter = new PageStyleAdapter();
        mRvBg.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mRvBg.setAdapter(mPageStyleAdapter);
        mPageStyleAdapter.refreshItems(Arrays.asList(drawables));

        mPageStyleAdapter.setPageStyleChecked(mPageStyle);

    }

    private void initPageMode() {
        switch (mPageMode) {
            case SIMULATION:
                mRbSimulation.setChecked(true);
                break;
            case COVER:
                mRbCover.setChecked(true);
                break;
            case SLIDE:
                mRbSlide.setChecked(true);
                break;
            case NONE:
                mRbNone.setChecked(true);
                break;
            case SCROLL:
                mRbScroll.setChecked(true);
                break;
        }
    }

    private void initPageCompose() {
        switch (mComposeMode) {
//            case MODE_4:
//                mRbCompose4.setChecked(true);
//                break;
            case MODE_3:
                mRbCompose3.setChecked(true);
                break;
            case MODE_2:
                mRbCompose2.setChecked(true);
                break;
            case MODE_0:
                mRbCompose0.setChecked(true);
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        //亮度调节
        mIvBrightnessMinus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() - 1;
                    if (progress < 0) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                }
        );
        mIvBrightnessPlus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() + 1;
                    if (progress > mSbBrightness.getMax()) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                    //设置进度
                    ReadSettingManager.getInstance().setBrightness(progress);
                }
        );

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mCbBrightnessAuto.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                    }
                    ReadSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );

        //字体大小调节
        mTvFontMinus.setOnClickListener(
                (v) -> {
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) - 1;
                    if (fontSize < 0) return;
                    if (fontSize == ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE)) {
                        mCbFontDefault.setChecked(true);
                        mSettingManager.setDefaultTextSize(true);
                    } else {
                        mCbFontDefault.setChecked(false);
                        mSettingManager.setDefaultTextSize(false);
                    }
                    mTvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mTvFontPlus.setOnClickListener(
                (v) -> {
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) + 1;
                    if (fontSize == ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE)) {
                        mCbFontDefault.setChecked(true);
                        mSettingManager.setDefaultTextSize(true);
                    } else {
                        mCbFontDefault.setChecked(false);
                        mSettingManager.setDefaultTextSize(false);
                    }
                    mTvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mCbFontDefault.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        int fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE);
                        mTvFont.setText(fontSize + "");
                        mPageLoader.setTextSize(fontSize);
                    }
                }
        );

        //Page Mode 切换
        mRgPageMode.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    PageMode pageMode;
                    switch (checkedId) {
                        case R.id.read_setting_rb_simulation:
                            pageMode = PageMode.SIMULATION;
                            break;
                        case R.id.read_setting_rb_cover:
                            pageMode = PageMode.COVER;
                            break;
                        case R.id.read_setting_rb_slide:
                            pageMode = PageMode.SLIDE;
                            break;
                        case R.id.read_setting_rb_scroll:
                            pageMode = PageMode.SCROLL;
                            break;
                        case R.id.read_setting_rb_none:
                            pageMode = PageMode.NONE;
                            break;
                        default:
                            pageMode = PageMode.SIMULATION;
                            break;
                    }
                    mPageLoader.setPageMode(pageMode);
                }
        );

        //Page Compose 切换
        mRgPageCompose.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    ComposeMode composeMode;
                    switch (checkedId) {
//                        case R.id.read_setting_rb_compose_4:
//                            composeMode = ComposeMode.MODE_4;
//                            break;
                        case R.id.read_setting_rb_compose_3:
                            composeMode = ComposeMode.MODE_3;
                            break;
                        case R.id.read_setting_rb_compose_2:
                            composeMode = ComposeMode.MODE_2;
                            break;
                        case R.id.read_setting_rb_compose_0:
                            composeMode = ComposeMode.MODE_0;
                            break;
                        default:
                            composeMode = ComposeMode.MODE_0;
                            break;
                    }
                    mPageLoader.setComposeMode(composeMode);
                }
        );

        //背景的点击事件
        mPageStyleAdapter.setOnItemClickListener(
                (view, pos) -> {
                    mPageLoader.setPageStyle(PageStyle.values()[pos]);
                    if (onSettingListener != null) {
                        onSettingListener.onPageStyleChange(PageStyle.values()[pos]);
                    }
                }
        );

        //更多设置
//        mTvMore.setOnClickListener(
//                (v) -> {
//                    Intent intent = new Intent(getContext(), MoreSettingActivity.class);
//                    mActivity.startActivityForResult(intent, ReadActivity.REQUEST_MORE_SETTING);
//                    //关闭当前设置
//                    dismiss();
//                }
//        );
    }

    @Override
    public void show() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        fullScreenImmersive(getWindow().getDecorView());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }


    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }

    public interface OnSettingListener {
        void onPageStyleChange(PageStyle value);
    }
}
