package com.inveno.xiandu.view.read;

/**
 * Created by newbiechen on 17-4-25.
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
    }
}