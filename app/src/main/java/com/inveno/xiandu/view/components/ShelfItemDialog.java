package com.inveno.xiandu.view.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.utils.ClickUtil;

/**
 * Created By huzheng
 * Date 2020/3/17
 * Des
 */
public class ShelfItemDialog extends BottomSheetDialog {

    private BookShelf book;
    private int position;
    private TextView name;
    private View cancel;
    private View del;
    private View download;
    private View move;
    private View top;
    private ShelfItemDialogListener shelfItemDialogListener;

    public void setShelfItemDialogListener(ShelfItemDialogListener shelfItemDialogListener) {
        this.shelfItemDialogListener = shelfItemDialogListener;
    }

    public ShelfItemDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ShelfItemDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected ShelfItemDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        //修改性别
        View bookOption = LayoutInflater.from(context).inflate(R.layout.bottomsheet_shelf_select, null);
        cancel = bookOption.findViewById(R.id.tv_bottomsheet_shelf_cancel);
        del = bookOption.findViewById(R.id.tv_bottomsheet_shelf_del);
        download = bookOption.findViewById(R.id.tv_bottomsheet_shelf_dowload);
        move = bookOption.findViewById(R.id.tv_bottomsheet_shelf_move);
        top = bookOption.findViewById(R.id.tv_bottomsheet_shelf_top);
        name = bookOption.findViewById(R.id.tv_bottomsheet_shelf_title);
        ClickUtil.bindSingleClick(cancel, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ClickUtil.bindSingleClick(del, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shelfItemDialogListener.onDel(position);
                dismiss();
            }
        });
        ClickUtil.bindSingleClick(download, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shelfItemDialogListener.onDownload(position);
                dismiss();
            }
        });
        ClickUtil.bindSingleClick(move, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shelfItemDialogListener.onMoveYanFei(position);
                dismiss();
            }
        });
        ClickUtil.bindSingleClick(top, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shelfItemDialogListener.onMoveTop(position);
                dismiss();
            }
        });
        setContentView(bookOption);
        try {
            ViewGroup parent = (ViewGroup) bookOption.getParent();
            parent.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBook(BookShelf book, int position) {
        this.book = book;
        this.position = position;
        name.setText(book.getBook_name());
    }

    public interface ShelfItemDialogListener{
        void onMoveTop(int position);
        void onMoveYanFei(int position);
        void onDownload(int position);
        void onDel(int position);
    }
}
