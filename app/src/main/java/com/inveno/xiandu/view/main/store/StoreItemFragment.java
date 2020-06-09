package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.utils.ClickUtil;
import com.inveno.xiandu.utils.GsonUtil;
import com.inveno.xiandu.view.BaseFragment;

/**
 * Created By huzheng
 * Date 2020/6/5
 * Des
 */
public class StoreItemFragment extends BaseFragment {

    String title;
    TextView textView;

    public StoreItemFragment(String title) {
        this.title = title;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_store_item, container, false);
        textView = view.findViewById(R.id.title);
        textView.setText(title);
        ClickUtil.bindSingleClick(textView, 500, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName(title);
                book.setAuthor("zheng.hu");
                ARouter.getInstance().build(ARouterPath.ACTIVITY_DETAIL_MAIN)
                        .withString("book", GsonUtil.objectToJson(book))
                        .navigation();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
