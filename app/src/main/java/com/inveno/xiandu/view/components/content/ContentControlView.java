package com.inveno.xiandu.view.components.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.AnimationTools;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.Toaster;


/**
 * Created By huzheng
 * Date 2020/3/25
 * Des 内容控制器
 */
public class ContentControlView extends RelativeLayout {

    //头部控制器
    private ViewGroup headerLayout;
    //底部控制器
    private ViewGroup bottomLayout;
    //触摸响应控件
//    private TouchView touchView;
    //设置
    private View setting;

    private View centerView;


    public static boolean showOption = false;

    public ContentControlView(Context context) {
        super(context);
    }

    public ContentControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContentControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        headerLayout = findViewById(R.id.layout_top);
        bottomLayout = findViewById(R.id.layout_bottom);
//        touchView = findViewById(R.id.view_touch);
        setting = findViewById(R.id.bt_setting);
        centerView = findViewById(R.id.center_view);

        centerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                centerClick();
                setVisibility(GONE);
            }
        });
        ClickUtil.bindSingleClick(setting, 100, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.showToastCenter(getContext(),"setting");
            }
        });

        setOnClickListener(null);
    }

    /**
     * 中间部分被点击
     */
    public void centerClick() {
        if (showOption) {
            headerLayout.setVisibility(GONE);
            bottomLayout.setVisibility(GONE);
            headerLayout.startAnimation(AnimationTools.getTopOut(getContext()));
            bottomLayout.startAnimation(AnimationTools.getBottomOut(getContext()));
        } else {
            headerLayout.setVisibility(VISIBLE);
            bottomLayout.setVisibility(VISIBLE);
            headerLayout.startAnimation(AnimationTools.getTopIn(getContext()));
            bottomLayout.startAnimation(AnimationTools.getBottomIn(getContext()));
        }
        showOption = !showOption;
    }
}
