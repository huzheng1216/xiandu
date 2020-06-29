package com.inveno.xiandu.view.read;

/**
 * Created by zheng.hu on 17-4-26.
 */

public interface BaseContract {

    interface BasePresenter<T> {

        void attachView(T view);

        void detachView();
    }

    interface BaseView {

        void showError();

        void complete();
    }
}
