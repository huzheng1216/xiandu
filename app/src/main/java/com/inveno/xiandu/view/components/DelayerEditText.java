package com.inveno.xiandu.view.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.StringTools;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * creat by: huzheng
 * date: 2019-12-06
 * description: 可控制输出频率的edittext
 */
public class DelayerEditText extends AppCompatEditText {

    private int duration = 400;//这这个间隔时间内，只输出一次结果，单位ms
    private OnDelayerTextChange onDelayerTextChange;
    private Disposable disposable;//上一次的任务

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOnDelayerTextChange(OnDelayerTextChange onDelayerTextChange) {
        this.onDelayerTextChange = onDelayerTextChange;
    }

    public DelayerEditText(Context context) {
        super(context);
    }

    public DelayerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DelayerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (StringTools.isNotEmpty(s.toString())) {
            doSearch(s.toString());
        }
    }

    private void doSearch(final String value) {

        int d = 0;//第一次不做延时
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            d = duration;
            LogUtils.H("取消上次的任务");
        }
        Observable.timer(d, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (onDelayerTextChange != null) {
                            onDelayerTextChange.textChange(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onDelayerTextChange != null) {
                            onDelayerTextChange.textChange(value);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public interface OnDelayerTextChange {
        void textChange(String title);
    }
}
