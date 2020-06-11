package com.inveno.xiandu.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inveno.xiandu.R;

/**
 * 作者：wyj
 * 时间：2020/3/30
 * 版本：${VERSION_NAME}
 * 功能：
 * 修改历史：
 */
public class IosTypeDialog extends Dialog {
    private Builder.DialogParams mDialogParams;
    private TextView dialog_title_tv;
    private TextView dialog_context_tv;
    private FrameLayout dialog_center_view;
    private TextView dialog_left_bt;
    private TextView dialog_center_bt;
    private TextView dialog_right_bt;

    private View dialog_bottom_view;
    private LinearLayout dialog_bottom_line;

    public IosTypeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ios_type_dialog);
        //设置背景透明，不然会出现白色直角问题
        Window window = getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
    }

    private void setParams(Builder.DialogParams dialogParams) {
        mDialogParams = dialogParams;
    }

    private void initView() {
        dialog_title_tv = findViewById(R.id.dialog_title_tv);
        dialog_context_tv = findViewById(R.id.dialog_context_tv);
        dialog_center_view = findViewById(R.id.dialog_center_view);
        dialog_left_bt = findViewById(R.id.dialog_left_bt);
        dialog_center_bt = findViewById(R.id.dialog_center_bt);
        dialog_right_bt = findViewById(R.id.dialog_right_bt);

        dialog_bottom_view = findViewById(R.id.dialog_bottom_view);
        dialog_bottom_line = findViewById(R.id.dialog_bottom_line);

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
        if (mDialogParams.centerView != null) {
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT);
//            dialog_center_view.setLayoutParams(params);
            dialog_center_view.addView(mDialogParams.centerView);
        }
        boolean hasButton = false;
        if (mDialogParams.leftButtonStr != null) {
            hasButton = true;
            dialog_left_bt.setText(mDialogParams.leftButtonStr);
            dialog_left_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogParams.leftButtonListener.onClick(v);
                }
            });
        } else {
            dialog_left_bt.setVisibility(View.GONE);
        }
        if (mDialogParams.centerButtonStr != null) {
            hasButton = true;
            dialog_center_bt.setText(mDialogParams.centerButtonStr);
            dialog_center_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogParams.centerButtonListener.onClick(v);
                }
            });
        } else {
            dialog_center_bt.setVisibility(View.GONE);
        }
        if (mDialogParams.rightButtonStr != null) {
            hasButton = true;
            dialog_right_bt.setText(mDialogParams.rightButtonStr);
            dialog_right_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogParams.rightButtonListener.onClick(v);
                }
            });
        } else {
            dialog_right_bt.setVisibility(View.GONE);
        }
        if (!hasButton) {
            dialog_bottom_line.setVisibility(View.GONE);
            dialog_bottom_view.setVisibility(View.GONE);
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

        public Builder setContext(View view) {
            dialogParams.centerView = view;
            return this;
        }

        public Builder setLeftButton(CharSequence charSequence, OnClickListener onClickListener) {
            dialogParams.leftButtonStr = charSequence;
            dialogParams.leftButtonListener = onClickListener;
            return this;
        }

        public Builder setCenterButton(CharSequence charSequence, OnClickListener onClickListener) {
            dialogParams.centerButtonStr = charSequence;
            dialogParams.centerButtonListener = onClickListener;
            return this;
        }

        public Builder setRightButton(CharSequence charSequence, OnClickListener onClickListener) {
            dialogParams.rightButtonStr = charSequence;
            dialogParams.rightButtonListener = onClickListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            dialogParams.cancelable = cancelable;
            return this;
        }

        public IosTypeDialog create() {
            final IosTypeDialog exeIosTypeDialog = new IosTypeDialog(mContext);
            exeIosTypeDialog.setParams(dialogParams);
            return exeIosTypeDialog;
        }

        public void show() {
            final IosTypeDialog iosTypeDialog = create();
            iosTypeDialog.dialogShow();
        }

        private class DialogParams {
            CharSequence title;
            CharSequence message;
            View centerView;
            CharSequence leftButtonStr;
            CharSequence rightButtonStr;
            CharSequence centerButtonStr;
            OnClickListener leftButtonListener;
            OnClickListener rightButtonListener;
            OnClickListener centerButtonListener;
            boolean cancelable = true;
        }
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
