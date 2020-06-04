package com.inveno.xiandu.view.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inveno.xiandu.R;

/**
 * Created By huzheng
 * Date 2020-02-17
 * Des 自定义dialog背景全透明无边框
 */
public class MyDialogTransparent extends Dialog {

    public MyDialogTransparent(@NonNull Context context) {
        //默认：背景全透明无边框样式
        super(context, R.style.MyDialog);
    }

    public MyDialogTransparent(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialogTransparent(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
