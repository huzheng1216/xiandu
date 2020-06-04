package com.inveno.xiandu.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.StringTools;

/**
 * Created By huzheng
 * Date 2020-02-17
 * Des 通用头部控件
 */
public class HeaderBar extends RelativeLayout {

    public final static int BACK = 0;//返回事件

    //标题部分
    private View titleLayout;
    private TextView title;
    private ImageView titleImg;
    //返回部分
    private View backLayout;
    private TextView back;
    private ImageView backImg;
    //更多部分
    private View optionLayout;
    private TextView option;
    private ImageView optionImg;
    private ProgressBar optionProgress;
    //数据
    private String titleStr;
    private String backStr;
    private String optionStr;
    private int titleImgResource = -1;
    private boolean showBackImg;
    private boolean showProgress;

    private OnActionListener onActionListener;

    public HeaderBar(Context context) {
        super(context);
    }

    public HeaderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeaderBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        titleLayout = findViewById(R.id.header_bar_title_layout);
        title = findViewById(R.id.header_bar_title_tv);
        titleImg = findViewById(R.id.header_bar_title_img);

        backLayout = findViewById(R.id.header_bar_back_layout);
        back = findViewById(R.id.header_bar_back_tv);
        backImg = findViewById(R.id.header_bar_back_img);

        optionLayout = findViewById(R.id.header_bar_option_layout);
        option = findViewById(R.id.header_bar_option_title);
        optionImg = findViewById(R.id.header_bar_option_more);
        optionProgress = findViewById(R.id.header_bar_option_progress);

        if (StringTools.isNotEmpty(titleStr)) {
            this.title.setText(titleStr);
            this.title.setVisibility(VISIBLE);
        }

        if (StringTools.isNotEmpty(backStr)) {
            this.back.setText(backStr);
            this.back.setVisibility(VISIBLE);
            ClickUtil.bindSingleClick(back, 400, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionListener != null) {
                        onActionListener.onAction(BACK, null);
                    }
                }
            });
        }

        if (titleImgResource > -1) {
            this.titleImg.setImageResource(titleImgResource);
            this.titleImg.setVisibility(VISIBLE);
        }

        if (showBackImg) {
            this.backImg.setVisibility(VISIBLE);
            ClickUtil.bindSingleClick(backImg, 400, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionListener != null) {
                        onActionListener.onAction(BACK, null);
                    }
                }
            });
        }

        if (showProgress) {
            this.optionProgress.setVisibility(VISIBLE);
        }

    }

    /**
     * 设置监听
     *
     * @param onActionListener
     */
    public void setListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public HeaderBar setTitle(String title) {
        this.titleStr = title;
        if (this.title != null) {
            this.title.setText(title);
            this.title.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * 设置标题图片
     *
     * @param resource
     * @return
     */
    public HeaderBar setTitleImg(int resource) {
        this.titleImgResource = resource;
        if (titleImg != null) {
            this.titleImg.setImageResource(resource);
            this.titleImg.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * 设置返回提示语
     *
     * @param backTitle
     * @return
     */
    public HeaderBar setBackTitle(String backTitle) {
        this.backStr = backTitle;
        if (back != null) {
            this.back.setText(backTitle);
            this.back.setVisibility(VISIBLE);
            ClickUtil.bindSingleClick(back, 400, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionListener != null) {
                        onActionListener.onAction(BACK, null);
                    }
                }
            });
        }
        return this;
    }

    /**
     * 显示返回图片
     * @return
     */
    public HeaderBar showBackImg() {
        this.showBackImg = true;
        if (backImg != null) {
            this.backImg.setVisibility(VISIBLE);
            ClickUtil.bindSingleClick(backImg, 400, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionListener != null) {
                        onActionListener.onAction(BACK, null);
                    }
                }
            });
        }
        return this;
    }

    /**
     * 显示进度
     * @return
     */
    public HeaderBar showProgress() {
        this.showProgress = true;
        if (optionProgress != null) {
            this.optionProgress.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * 隐藏进度
     */
    public void hitenProgress() {
        this.optionProgress.setVisibility(GONE);
    }

    /**
     * 头部事件
     */
    public interface OnActionListener {
        void onAction(int action, Object object);
    }

}
