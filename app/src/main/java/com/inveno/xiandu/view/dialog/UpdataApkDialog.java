package com.inveno.xiandu.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.inveno.xiandu.R;

/**
 * @author yongji.wang
 * @date 2020/7/6 17:43
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UpdataApkDialog extends Dialog {

    private Builder.DialogParams mDialogParams;
    private TextView dialog_title_tv;
    private TextView dialog_context_tv;
    private TextView dialog_updata_bt;
    private TextView dialog_cancel_bt;
    private TextView dialog_advice;

    public UpdataApkDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_updata_apk);
        //设置背景透明，不然会出现白色直角问题
        Window window = getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
    }

    private void setParams(UpdataApkDialog.Builder.DialogParams dialogParams) {
        mDialogParams = dialogParams;
    }

    private void initView() {
        dialog_title_tv = findViewById(R.id.dialog_title_tv);
        dialog_context_tv = findViewById(R.id.dialog_context_tv);
        dialog_updata_bt = findViewById(R.id.dialog_updata_bt);
        dialog_cancel_bt = findViewById(R.id.dialog_cancel_bt);
        dialog_advice = findViewById(R.id.dialog_advice);

        setData();
    }

    private void setData() {
        if (TextUtils.isEmpty(mDialogParams.title)) {
            dialog_title_tv.setVisibility(View.GONE);
        } else {
            dialog_title_tv.setText(mDialogParams.title);
        }
        if (!TextUtils.isEmpty(mDialogParams.message)) {
            dialog_context_tv.setText(mDialogParams.message);
        } else {
            dialog_context_tv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mDialogParams.updataButtonName)) {
            dialog_updata_bt.setText(mDialogParams.updataButtonName);
            dialog_updata_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogParams.updataButtonListener.onClick(v);
                }
            });
        } else {
            dialog_updata_bt.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mDialogParams.cancelButtonName)) {
            dialog_cancel_bt.setText(mDialogParams.cancelButtonName);
            dialog_cancel_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogParams.cancelButtonListener.onClick(v);
                }
            });
        } else {
            dialog_cancel_bt.setVisibility(View.GONE);
        }
        if (mDialogParams.needAdvice) {
            dialog_advice.setVisibility(View.VISIBLE);
        } else {
            dialog_advice.setVisibility(View.GONE);
        }
        this.setCancelable(mDialogParams.cancelable);
    }

    private void dialogShow() {
        this.show();
    }

    public static class Builder {
        private final DialogParams dialogParams;
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
            dialogParams = new DialogParams();
        }

        public Builder setTitle(CharSequence charSequence) {
            dialogParams.title = charSequence;
            return this;
        }

        public Builder setContext(CharSequence charSequence) {
            dialogParams.message = charSequence;
            return this;
        }

        public Builder setUpdataButtonListener(String buttonName, OnClickListener onClickListener) {
            dialogParams.updataButtonName = buttonName;
            dialogParams.updataButtonListener = onClickListener;
            return this;
        }

        public Builder setCancelButtonListener(String buttonName, OnClickListener onClickListener) {
            dialogParams.cancelButtonName = buttonName;
            dialogParams.cancelButtonListener = onClickListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            dialogParams.cancelable = cancelable;
            return this;
        }

        public Builder setNeedAdvice(boolean needAdvice) {
            dialogParams.needAdvice = needAdvice;
            return this;
        }

        public UpdataApkDialog create(Context context) {
            final UpdataApkDialog updataApkDialog = new UpdataApkDialog(context);
            updataApkDialog.setParams(dialogParams);
            return updataApkDialog;
        }

        public UpdataApkDialog create() {
            final UpdataApkDialog updataApkDialog = new UpdataApkDialog(mContext);
            updataApkDialog.setParams(dialogParams);
            return updataApkDialog;
        }

        public void show() {
            final UpdataApkDialog updataApkDialog = create();
            updataApkDialog.dialogShow();
        }

        private class DialogParams {
            CharSequence title;
            CharSequence message;
            CharSequence cancelButtonName;
            CharSequence updataButtonName;
            boolean needAdvice = true;
            OnClickListener updataButtonListener;
            OnClickListener cancelButtonListener;
            boolean cancelable = true;
        }

        public interface OnClickListener {
            void onClick(View v);
        }
    }
}
