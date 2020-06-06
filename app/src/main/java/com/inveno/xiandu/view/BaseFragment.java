package com.inveno.xiandu.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inveno.xiandu.view.event.EventNightModeChange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * creat by: huzheng
 * date: 2019-09-17
 * description:
 * 基础fragment
 * 防止重复inflater
 */
public abstract class BaseFragment extends Fragment {

    protected View contentView = null;
    private boolean firstVisble = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = initView(inflater, container, savedInstanceState);
        }
        if (contentView != null) {
            return contentView;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * 当fragment与viewpager、FragmentPagerAdapter一起使用时，切换页面时会调用此方法
     *
     * @param isVisibleToUser 是否对用户可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        boolean change = isVisibleToUser != getUserVisibleHint();
        super.setUserVisibleHint(isVisibleToUser);
        // 在viewpager中，创建fragment时就会调用这个方法，但这时还没有resume，为了避免重复调用visible和invisible，
        // 只有当fragment状态是resumed并且初始化完毕后才进行visible和invisible的回调
        if (isResumed() && change) {
            if (getUserVisibleHint()) {
                onVisible(firstVisble);
                firstVisble = false;
            } else {
                onInVisible();
            }
        }
    }

    /**
     * 当使用show/hide方法时，会触发此回调
     *
     * @param hidden fragment是否被隐藏
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onInVisible();
        } else {
            onVisible(firstVisble);
            firstVisble = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // onResume并不代表fragment可见
        // 如果是在viewpager里，就需要判断getUserVisibleHint，不在viewpager时，getUserVisibleHint默认为true
        // 如果是其它情况，就通过isHidden判断，因为show/hide时会改变isHidden的状态
        // 所以，只有当fragment原来是可见状态时，进入onResume就回调onVisible
        if (getUserVisibleHint() && !isHidden()) {
            onVisible(firstVisble);
            firstVisble = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // onPause时也需要判断，如果当前fragment在viewpager中不可见，就已经回调过了，onPause时也就不需要再次回调onInvisible了
        // 所以，只有当fragment是可见状态时进入onPause才加调onInvisible
        if (getUserVisibleHint() && !isHidden()) {
            onInVisible();
        }
    }

    //切换黑夜白天模式
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayNightChange(EventNightModeChange eventNightModeChange) {
    }

    @Override
    public void onDestroyView() {
        if (contentView != null && contentView.getParent() != null)
            ((ViewGroup) contentView.getParent()).removeView(contentView);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    //当前页面可见
    protected void onVisible(Boolean firstVisble) {
    };

    //当前页面不可见
    protected void onInVisible() {
    };
}
