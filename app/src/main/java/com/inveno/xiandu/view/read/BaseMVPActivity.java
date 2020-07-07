package com.inveno.xiandu.view.read;

import com.inveno.xiandu.utils.ActivityManager;

/**
 * Created by zheng.hu on 17-4-25.
 */

public abstract class BaseMVPActivity<T extends BaseContract.BasePresenter> extends BaseReadActivity{

    protected T mPresenter;

    protected abstract T bindPresenter();

    @Override
    protected void processLogic() {
        attachView(bindPresenter());
    }

    private void attachView(T presenter){
        mPresenter = presenter;
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        ActivityManager.getAppManager().finishActivity(this);
    }
}