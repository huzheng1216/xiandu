package com.inveno.xiandu.view.main.shelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.db.SQL;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.BaseFragment;
import com.inveno.xiandu.view.components.GridSpacingItemDecoration;
import com.inveno.xiandu.view.components.ShelfItemDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By huzheng
 * Date 2020-02-28
 * Des 书架界面-书架
 */
public class ShelfFragment extends BaseFragment implements ShelfItemDialog.ShelfItemDialogListener {

    //基础控件
    private RecyclerView recyclerView;
    private ShelfAdapter shelfAdapter;

    //书籍选项
    private ShelfItemDialog bottomSheetDialog;

    //数据
    private BookShelf bookShelf;
    private List<Book> data = new ArrayList<>();

    public ShelfFragment(BookShelf bookShelf) {
        super();
        this.bookShelf = bookShelf;
    }

    @Override
    public ViewGroup initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.fragment_bookrack_main_shelf, null);
        recyclerView = inflate.findViewById(R.id.recycleview_fragment_bookrack_rack);

        shelfAdapter = new ShelfAdapter(getContext(), data);
        shelfAdapter.setShelfAdapterListener(new ShelfAdapter.ShelfAdapterListener() {
//            @Override
//            public void onAddClick() {
//                ARouter.getInstance().build(ARouterPath.ACTIVITY_SEARCH)
//                        .navigation();
//            }

            @Override
            public void onBookClick(int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_CATALOG_MAIN)
                        .withString("json", GsonUtil.objectToJson(data.get(position)))
                        .navigation();
            }

            @Override
            public void onBookLongClick(int position) {
                Toaster.showToastCenter(getContext(), "长按书籍：" + position);
                showOption(position);
            }
        });
        // 设置adapter
        recyclerView.setAdapter(shelfAdapter);
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);  // 垂直排列
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, getResources().getDimensionPixelSize(R.dimen.adapter_search_history), true));
        recyclerView.setLayoutManager(layoutManager);

        //初始化书籍操作
        bottomSheetDialog = new ShelfItemDialog(getContext());
        bottomSheetDialog.setShelfItemDialogListener(this);
        return inflate;
    }

    private void showOption(int position) {
        bottomSheetDialog.setBook(data.get(position), position);
        bottomSheetDialog.show();
    }

    @Override
    protected void onVisible(Boolean firstVisble) {
        LogUtils.H("书架可见：" + firstVisble);
        initData();
    }

    private void initData() {
        data.clear();
        data.addAll(SQL.getInstance(getContext()).getAllBookByShelfId(bookShelf.getId()));
        LogUtils.H("书架：" + data.size());
        shelfAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMoveTop(int position) {
        Book remove = data.remove(position);
        data.add(0, remove);
        shelfAdapter.notifyDataSetChanged();
        SQL.getInstance(getContext()).updateBook(remove);
    }

    @Override
    public void onMoveYanFei(int position) {
        Book remove = data.remove(position);
        shelfAdapter.notifyDataSetChanged();
//        remove.setShelfId("yf");
        SQL.getInstance(getContext()).updateBook(remove);
    }

    @Override
    public void onDownload(int position) {
        Toaster.showToastCenter(getContext(), "下载：" + position);
    }

    @Override
    public void onDel(int position) {
        Book remove = data.remove(position);
        shelfAdapter.notifyDataSetChanged();
        SQL.getInstance(getContext()).delBoot(remove);
    }
}
