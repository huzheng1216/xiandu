package com.inveno.xiandu.view.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.utils.Toaster;

/**
 * Created By huzheng
 * Date 2020/6/11
 * Des 书架弹窗
 */
public class PopupWindowShelfItem extends PopupWindow {

    View conentView;
    Activity context;
    PopListener popListener;

    public void setPopListener(PopListener popListener) {
        this.popListener = popListener;
    }

    public PopupWindowShelfItem(Activity context){
        this.context = context;
        initPopupWindow();
    }

    private void initPopupWindow() {
        //使用view来引入布局
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_shelf, null);
        View del = conentView.findViewById(R.id.del);

        //获取popupwindow的高度与宽度
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 2 + 50);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //布局控件初始化与监听设置
        conentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popListener.onDel(bookbrack);
                dismiss();
            }
        });
    }


    /**
     * 显示popupWindow的方式设置，当然可以有别的方式。
     *一会会列出其他方法
     * @param parent
     */
    Bookbrack bookbrack;
    public void showPopupWindow(Bookbrack bookbrack, View parent) {
        this.bookbrack = bookbrack;
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            int[] location = new int[2];
            //获取在整个屏幕内的绝对坐标
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0], location[1] + parent.getHeight());
        } else {
            this.dismiss();
        }
    }

    public interface PopListener{
        void onDel(Bookbrack bookbrack);
    }
}
